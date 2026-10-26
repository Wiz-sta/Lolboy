package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AiAssistantScreen
import com.example.ui.BookmarksHistoryDialog
import com.example.ui.BrowserScreen
import com.example.ui.BrowserViewModel
import com.example.ui.ReaderModeDialog
import com.example.ui.SettingsDialog
import com.example.ui.TabsSwitcherDialog
import com.example.ui.TutorFinderScreen
import com.example.ui.theme.SakibBrowserTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: BrowserViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

            val isDark = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            SakibBrowserTheme(darkTheme = isDark) {
                SakibBrowserApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SakibBrowserApp(viewModel: BrowserViewModel) {
    val isTabsSwitcherOpen by viewModel.isTabsSwitcherOpen.collectAsStateWithLifecycle()
    val isBookmarksHistoryOpen by viewModel.isBookmarksHistoryOpen.collectAsStateWithLifecycle()
    val isTutorFinderOpen by viewModel.isTutorFinderOpen.collectAsStateWithLifecycle()
    val isAiAssistantOpen by viewModel.isAiAssistantOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isReaderModeOpen by viewModel.isReaderModeOpen.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        BrowserScreen(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )

        // Modal Dialogs & Sheets
        if (isTabsSwitcherOpen) {
            TabsSwitcherDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setTabsSwitcherOpen(false) }
            )
        }

        if (isBookmarksHistoryOpen) {
            BookmarksHistoryDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setBookmarksHistoryOpen(false) }
            )
        }

        if (isTutorFinderOpen) {
            TutorFinderScreen(
                viewModel = viewModel,
                onDismiss = { viewModel.setTutorFinderOpen(false) }
            )
        }

        if (isAiAssistantOpen) {
            AiAssistantScreen(
                viewModel = viewModel,
                onDismiss = { viewModel.setAiAssistantOpen(false) }
            )
        }

        if (isSettingsOpen) {
            SettingsDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setSettingsOpen(false) }
            )
        }

        if (isReaderModeOpen) {
            ReaderModeDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.closeReaderMode() }
            )
        }
    }
}
