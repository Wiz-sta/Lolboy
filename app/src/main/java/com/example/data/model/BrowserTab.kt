package com.example.data.model

import java.util.UUID

data class BrowserTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "about:blank",
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isIncognito: Boolean = false,
    val isDesktopMode: Boolean = false,
    val isReaderMode: Boolean = false,
    val isHomepage: Boolean = true
)

enum class SearchEngine(
    val displayName: String,
    val searchUrl: String,
    val homeUrl: String,
    val iconName: String
) {
    GOOGLE(
        "Google",
        "https://www.google.com/search?q=",
        "https://www.google.com",
        "G"
    ),
    DUCKDUCKGO(
        "DuckDuckGo",
        "https://duckduckgo.com/?q=",
        "https://duckduckgo.com",
        "DDG"
    ),
    BING(
        "Bing",
        "https://www.bing.com/search?q=",
        "https://www.bing.com",
        "B"
    ),
    YAHOO(
        "Yahoo",
        "https://search.yahoo.com/search?p=",
        "https://search.yahoo.com",
        "Y"
    )
}

data class QuickShortcut(
    val title: String,
    val url: String,
    val category: String,
    val iconLetter: String,
    val badgeColorHex: Long
)
