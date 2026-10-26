package com.example.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val activeTabIndex by viewModel.activeTabIndex.collectAsStateWithLifecycle()
    val currentTab = tabs.getOrNull(activeTabIndex) ?: return
    val webViewCommand by viewModel.webViewAction.collectAsStateWithLifecycle()
    val jsEnabled by viewModel.javaScriptEnabled.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isEditingAddress by remember { mutableStateOf(false) }
    var addressInput by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showSslDialog by remember { mutableStateOf(false) }

    val isCurrentBookmarked = bookmarks.any { it.url == currentTab.url }

    // Intercept hardware Back button
    BackHandler {
        if (isEditingAddress) {
            isEditingAddress = false
        } else if (webViewInstance?.canGoBack() == true) {
            webViewInstance?.goBack()
        } else if (!currentTab.isHomepage) {
            viewModel.goHome()
        }
    }

    // Execute WebView commands
    LaunchedEffect(webViewCommand) {
        val command = webViewCommand ?: return@LaunchedEffect
        when (command) {
            is BrowserViewModel.WebViewCommand.GoBack -> webViewInstance?.goBack()
            is BrowserViewModel.WebViewCommand.GoForward -> webViewInstance?.goForward()
            is BrowserViewModel.WebViewCommand.Reload -> webViewInstance?.reload()
            is BrowserViewModel.WebViewCommand.LoadUrl -> {
                if (command.url != "about:blank") {
                    webViewInstance?.loadUrl(command.url)
                }
            }
        }
        viewModel.clearWebViewCommand()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP ADDRESS & CONTROLS BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditingAddress) {
                        OutlinedTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            placeholder = { Text("Search or type URL", fontSize = 13.sp) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    viewModel.submitSearch(addressInput)
                                    isEditingAddress = false
                                }
                            ),
                            trailingIcon = {
                                IconButton(onClick = { isEditingAddress = false }) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel edit")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("address_bar_edit_input")
                        )
                    } else {
                        // Display Address Bar
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    addressInput = if (currentTab.url == "about:blank") "" else currentTab.url
                                    isEditingAddress = true
                                }
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Security Lock Icon
                            val isSecure = currentTab.url.startsWith("https://")
                            IconButton(
                                onClick = { showSslDialog = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSecure) Icons.Default.Lock else Icons.Default.Security,
                                    contentDescription = "Security info",
                                    tint = if (isSecure) MaterialTheme.colorScheme.primary else Color(0xFFEF4444),
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (currentTab.title.isNotBlank()) currentTab.title else "Search or type URL",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (currentTab.url != "about:blank" && currentTab.url.isNotBlank()) {
                                    Text(
                                        text = currentTab.url.removePrefix("https://").removePrefix("http://"),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Incognito badge indicator if applicable
                            if (currentTab.isIncognito) {
                                Text(
                                    text = "🕶️",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                            }

                            // Reader Mode Button
                            IconButton(
                                onClick = { viewModel.toggleReaderMode() },
                                modifier = Modifier.size(28.dp).testTag("reader_mode_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChromeReaderMode,
                                    contentDescription = "Reader Mode",
                                    tint = if (currentTab.isReaderMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Reload / Stop button
                    IconButton(
                        onClick = {
                            if (currentTab.isLoading) {
                                webViewInstance?.stopLoading()
                            } else {
                                webViewInstance?.reload()
                            }
                        },
                        modifier = Modifier.testTag("reload_button")
                    ) {
                        Icon(
                            imageVector = if (currentTab.isLoading) Icons.Default.Close else Icons.Default.Refresh,
                            contentDescription = if (currentTab.isLoading) "Stop" else "Reload",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // More Menu
                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.testTag("overflow_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (isCurrentBookmarked) "Remove Bookmark" else "Bookmark Page") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (isCurrentBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    viewModel.toggleBookmarkCurrentPage()
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Bookmarks & History") },
                                leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                                onClick = {
                                    viewModel.setBookmarksHistoryOpen(true)
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(if (currentTab.isDesktopMode) "Mobile Site" else "Desktop Site") },
                                leadingIcon = { Icon(Icons.Default.DesktopWindows, contentDescription = null) },
                                onClick = {
                                    viewModel.toggleDesktopMode()
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Share Page") },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                                onClick = {
                                    showMenu = false
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, currentTab.url)
                                        putExtra(Intent.EXTRA_SUBJECT, currentTab.title)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Webpage"))
                                }
                            )

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Find a Tutor") },
                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF38BDF8)) },
                                onClick = {
                                    viewModel.setTutorFinderOpen(true)
                                    showMenu = false
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("AI Study Assistant") },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFA855F7)) },
                                onClick = {
                                    viewModel.setAiAssistantOpen(true)
                                    showMenu = false
                                }
                            )

                            HorizontalDivider()

                            DropdownMenuItem(
                                text = { Text("Settings") },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                onClick = {
                                    viewModel.setSettingsOpen(true)
                                    showMenu = false
                                }
                            )
                        }
                    }
                }

                // Page Loading Progress Indicator
                if (currentTab.isLoading) {
                    LinearProgressIndicator(
                        progress = { (currentTab.progress.coerceIn(5, 100)) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.5.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        // MAIN CONTENT AREA: HOME SCREEN vs WEBVIEW
        Box(modifier = Modifier.weight(1f)) {
            if (currentTab.isHomepage) {
                HomeScreen(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )

                            settings.apply {
                                javaScriptEnabled = jsEnabled
                                domStorageEnabled = true
                                databaseEnabled = true
                                builtInZoomControls = true
                                displayZoomControls = false
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

                                if (currentTab.isDesktopMode) {
                                    userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    if (url.startsWith("http://") || url.startsWith("https://")) {
                                        return false
                                    }
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        ctx.startActivity(intent)
                                        true
                                    } catch (e: Exception) {
                                        true
                                    }
                                }

                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    url?.let { viewModel.onPageStarted(it) }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    url?.let {
                                        viewModel.onPageFinished(
                                            url = it,
                                            title = view?.title,
                                            canGoBack = view?.canGoBack() ?: false,
                                            canGoForward = view?.canGoForward() ?: false
                                        )
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    viewModel.onProgressChanged(newProgress)
                                }

                                override fun onReceivedTitle(view: WebView?, title: String?) {
                                    super.onReceivedTitle(view, title)
                                    title?.let { viewModel.onTitleChanged(it) }
                                }
                            }

                            if (currentTab.url != "about:blank") {
                                loadUrl(currentTab.url)
                            }
                            webViewInstance = this
                        }
                    },
                    update = { view ->
                        webViewInstance = view
                        view.settings.javaScriptEnabled = jsEnabled
                        if (currentTab.isDesktopMode) {
                            view.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // BOTTOM NAVIGATION BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = {
                        if (webViewInstance?.canGoBack() == true) {
                            webViewInstance?.goBack()
                        } else if (!currentTab.isHomepage) {
                            viewModel.goHome()
                        }
                    },
                    enabled = currentTab.canGoBack || !currentTab.isHomepage,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = if (currentTab.canGoBack || !currentTab.isHomepage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                // Forward Button
                IconButton(
                    onClick = { webViewInstance?.goForward() },
                    enabled = currentTab.canGoForward,
                    modifier = Modifier.testTag("nav_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Forward",
                        tint = if (currentTab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                }

                // Home Button
                IconButton(
                    onClick = { viewModel.goHome() },
                    modifier = Modifier.testTag("nav_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        tint = if (currentTab.isHomepage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Tabs Switcher with Numeric Badge
                IconButton(
                    onClick = { viewModel.setTabsSwitcherOpen(true) },
                    modifier = Modifier.testTag("nav_tabs_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(1.5.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${tabs.size}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // AI Assistant Quick Launch Button
                IconButton(
                    onClick = { viewModel.setAiAssistantOpen(true) },
                    modifier = Modifier.testTag("nav_ai_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFA855F7), Color(0xFF38BDF8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // SSL / Security Dialog
    if (showSslDialog) {
        val isHttps = currentTab.url.startsWith("https://")
        AlertDialog(
            onDismissRequest = { showSslDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (isHttps) Icons.Default.Lock else Icons.Default.Security,
                        contentDescription = null,
                        tint = if (isHttps) MaterialTheme.colorScheme.primary else Color(0xFFEF4444)
                    )
                    Text(if (isHttps) "Connection is Secure" else "Connection Not Secure")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isHttps) {
                            "Your information (such as passwords or credit card numbers) is private when it is sent to this site via HTTPS encryption."
                        } else {
                            "You should not enter any sensitive information on this site because it doesn't use a secure HTTPS connection."
                        },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "URL: ${currentTab.url}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSslDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
