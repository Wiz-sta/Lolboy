package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.DuckDuckGoSearchApi
import com.example.data.api.GeminiClient
import com.example.data.api.InstantSearchResult
import com.example.data.api.WeatherClient
import com.example.data.db.BookmarkEntity
import com.example.data.db.BrowserDatabase
import com.example.data.db.HistoryEntity
import com.example.data.db.SavedArticleEntity
import com.example.data.db.SearchHistoryEntity
import com.example.data.model.AiMessage
import com.example.data.model.BrowserTab
import com.example.data.model.MessageSender
import com.example.data.model.QuickShortcut
import com.example.data.model.SearchEngine
import com.example.data.model.Tutor
import com.example.data.model.TutorCategory
import com.example.data.model.TutorMode
import com.example.data.model.WeatherData
import com.example.data.repository.BrowserRepository
import com.example.data.repository.TutorRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BrowserDatabase.getInstance(application)
    private val repository = BrowserRepository(db.browserDao())
    private val tutorRepository = TutorRepository()

    // Database flows
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<SearchHistoryEntity>> = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedArticles: StateFlow<List<SavedArticleEntity>> = repository.savedArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tabs Management
    private val _tabs = MutableStateFlow<List<BrowserTab>>(listOf(BrowserTab(isHomepage = true)))
    val tabs: StateFlow<List<BrowserTab>> = _tabs.asStateFlow()

    private val _activeTabIndex = MutableStateFlow(0)
    val activeTabIndex: StateFlow<Int> = _activeTabIndex.asStateFlow()

    val activeTab: BrowserTab
        get() = _tabs.value.getOrNull(_activeTabIndex.value) ?: BrowserTab()

    // Quick Shortcuts
    val quickShortcuts = listOf(
        QuickShortcut("Google", "https://www.google.com", "Search", "G", 0xFF4285F4),
        QuickShortcut("10 Minute School", "https://10minuteschool.com", "Education", "10", 0xFF10B981),
        QuickShortcut("Khan Academy", "https://www.khanacademy.org", "Education", "K", 0xFF14B8A6),
        QuickShortcut("YouTube", "https://www.youtube.com", "Video", "Y", 0xFFEF4444),
        QuickShortcut("Wikipedia", "https://www.wikipedia.org", "Reference", "W", 0xFF6B7280),
        QuickShortcut("Shikho", "https://shikho.com", "Education", "S", 0xFF8B5CF6),
        QuickShortcut("Coursera", "https://www.coursera.org", "College", "C", 0xFF0284C7),
        QuickShortcut("BBC Learning", "https://www.bbc.co.uk/learningenglish", "English", "B", 0xFFEC4899)
    )

    // Search Engine & Settings
    private val _selectedSearchEngine = MutableStateFlow(SearchEngine.DUCKDUCKGO)
    val selectedSearchEngine: StateFlow<SearchEngine> = _selectedSearchEngine.asStateFlow()

    private val _themeMode = MutableStateFlow("DARK") // "DARK", "LIGHT", "SYSTEM"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _javaScriptEnabled = MutableStateFlow(true)
    val javaScriptEnabled: StateFlow<Boolean> = _javaScriptEnabled.asStateFlow()

    // Search & Suggestions
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    private val _instantResults = MutableStateFlow<List<InstantSearchResult>>(emptyList())
    val instantResults: StateFlow<List<InstantSearchResult>> = _instantResults.asStateFlow()

    private var searchDebounceJob: Job? = null

    // Weather
    private val _weatherData = MutableStateFlow<WeatherData?>(null)
    val weatherData: StateFlow<WeatherData?> = _weatherData.asStateFlow()

    // Modals / Fullscreen Subviews
    private val _isTabsSwitcherOpen = MutableStateFlow(false)
    val isTabsSwitcherOpen: StateFlow<Boolean> = _isTabsSwitcherOpen.asStateFlow()

    private val _isBookmarksHistoryOpen = MutableStateFlow(false)
    val isBookmarksHistoryOpen: StateFlow<Boolean> = _isBookmarksHistoryOpen.asStateFlow()

    private val _isTutorFinderOpen = MutableStateFlow(false)
    val isTutorFinderOpen: StateFlow<Boolean> = _isTutorFinderOpen.asStateFlow()

    private val _isAiAssistantOpen = MutableStateFlow(false)
    val isAiAssistantOpen: StateFlow<Boolean> = _isAiAssistantOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isReaderModeOpen = MutableStateFlow(false)
    val isReaderModeOpen: StateFlow<Boolean> = _isReaderModeOpen.asStateFlow()

    // Tutor Finder state
    private val _tutorCategory = MutableStateFlow(TutorCategory.ALL)
    val tutorCategory: StateFlow<TutorCategory> = _tutorCategory.asStateFlow()

    private val _tutorMode = MutableStateFlow(TutorMode.ALL)
    val tutorMode: StateFlow<TutorMode> = _tutorMode.asStateFlow()

    private val _tutorLanguage = MutableStateFlow("All")
    val tutorLanguage: StateFlow<String> = _tutorLanguage.asStateFlow()

    private val _tutorQuery = MutableStateFlow("")
    val tutorQuery: StateFlow<String> = _tutorQuery.asStateFlow()

    private val _tutorsList = MutableStateFlow<List<Tutor>>(emptyList())
    val tutorsList: StateFlow<List<Tutor>> = _tutorsList.asStateFlow()

    private val _selectedTutorDetail = MutableStateFlow<Tutor?>(null)
    val selectedTutorDetail: StateFlow<Tutor?> = _selectedTutorDetail.asStateFlow()

    // AI Study Assistant state
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                sender = MessageSender.ASSISTANT,
                text = "👋 আসসালামু আলাইকুম! আমি Sakib Browser-এর AI Study Assistant।\n\nআপনি যেকোনো কঠিন বিষয় সহজ ভাষায় বুঝতে, গণিত বা বিজ্ঞানের সমস্যা সমাধান করতে, ইংরেজি ব্যাকরণ শিখতে অথবা ওয়েব আর্টিকেল সংক্ষেপ করতে আমাকে প্রশ্ন করতে পারেন। আমি বাংলা ও ইংরেজি উভয় ভাষায় সাহায্য করতে পারি!"
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Active Reader Mode Content
    private val _readerContent = MutableStateFlow<Pair<String, String>?>(null) // Title to Body text
    val readerContent: StateFlow<Pair<String, String>?> = _readerContent.asStateFlow()

    // Command to trigger reload or navigate in WebView
    private val _webViewAction = MutableStateFlow<WebViewCommand?>(null)
    val webViewAction: StateFlow<WebViewCommand?> = _webViewAction.asStateFlow()

    sealed class WebViewCommand {
        object GoBack : WebViewCommand()
        object GoForward : WebViewCommand()
        object Reload : WebViewCommand()
        data class LoadUrl(val url: String) : WebViewCommand()
    }

    init {
        loadWeather()
        refreshTutors()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            try {
                val data = WeatherClient.getCurrentWeather("Dhaka")
                _weatherData.value = data
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()
        if (query.isBlank()) {
            _searchSuggestions.value = emptyList()
            _instantResults.value = emptyList()
            return
        }

        searchDebounceJob = viewModelScope.launch {
            delay(250)
            val suggestions = DuckDuckGoSearchApi.getSuggestions(query)
            _searchSuggestions.value = suggestions

            val instant = DuckDuckGoSearchApi.getInstantAnswers(query)
            _instantResults.value = instant
        }
    }

    fun submitSearch(input: String) {
        val trimmed = input.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            repository.addSearchQuery(trimmed)
        }

        val url = resolveToUrl(trimmed)
        loadUrlInActiveTab(url)
        _searchQuery.value = ""
        _searchSuggestions.value = emptyList()
        _instantResults.value = emptyList()
    }

    private fun resolveToUrl(input: String): String {
        val lower = input.lowercase()
        return if (lower.startsWith("http://") || lower.startsWith("https://")) {
            input
        } else if (lower.contains(" ") || !lower.contains(".")) {
            val encoded = URLEncoder.encode(input, StandardCharsets.UTF_8.name())
            "${_selectedSearchEngine.value.searchUrl}$encoded"
        } else {
            "https://$input"
        }
    }

    fun loadUrlInActiveTab(url: String) {
        val currentIndex = _activeTabIndex.value
        val currentTabs = _tabs.value.toMutableList()
        if (currentIndex in currentTabs.indices) {
            val tab = currentTabs[currentIndex].copy(
                url = url,
                isHomepage = false,
                isLoading = true,
                progress = 10
            )
            currentTabs[currentIndex] = tab
            _tabs.value = currentTabs
            _webViewAction.value = WebViewCommand.LoadUrl(url)
        }
    }

    fun clearWebViewCommand() {
        _webViewAction.value = null
    }

    // Tab Operations
    fun openNewTab(url: String = "about:blank", isIncognito: Boolean = false) {
        val isHome = url == "about:blank"
        val newTab = BrowserTab(
            title = if (isHome) (if (isIncognito) "Incognito Tab" else "New Tab") else url,
            url = url,
            isIncognito = isIncognito,
            isHomepage = isHome
        )
        val updated = _tabs.value + newTab
        _tabs.value = updated
        _activeTabIndex.value = updated.size - 1
        _isTabsSwitcherOpen.value = false
        if (!isHome) {
            _webViewAction.value = WebViewCommand.LoadUrl(url)
        }
    }

    fun closeTab(index: Int) {
        val currentTabs = _tabs.value.toMutableList()
        if (currentTabs.size <= 1) {
            // Reset to clean homepage
            _tabs.value = listOf(BrowserTab(isHomepage = true))
            _activeTabIndex.value = 0
            return
        }

        currentTabs.removeAt(index)
        _tabs.value = currentTabs
        val newActiveIndex = if (_activeTabIndex.value >= currentTabs.size) {
            currentTabs.size - 1
        } else if (_activeTabIndex.value > index) {
            _activeTabIndex.value - 1
        } else {
            _activeTabIndex.value
        }
        _activeTabIndex.value = newActiveIndex
    }

    fun selectTab(index: Int) {
        if (index in _tabs.value.indices) {
            _activeTabIndex.value = index
            _isTabsSwitcherOpen.value = false
        }
    }

    fun closeAllTabs() {
        _tabs.value = listOf(BrowserTab(isHomepage = true))
        _activeTabIndex.value = 0
        _isTabsSwitcherOpen.value = false
    }

    // Web Navigation actions
    fun goBack() {
        _webViewAction.value = WebViewCommand.GoBack
    }

    fun goForward() {
        _webViewAction.value = WebViewCommand.GoForward
    }

    fun reload() {
        _webViewAction.value = WebViewCommand.Reload
    }

    fun goHome() {
        val currentIndex = _activeTabIndex.value
        val currentTabs = _tabs.value.toMutableList()
        if (currentIndex in currentTabs.indices) {
            val tab = currentTabs[currentIndex].copy(
                isHomepage = true,
                url = "about:blank",
                title = if (currentTabs[currentIndex].isIncognito) "Incognito Tab" else "New Tab",
                isLoading = false,
                progress = 0
            )
            currentTabs[currentIndex] = tab
            _tabs.value = currentTabs
        }
    }

    fun toggleDesktopMode() {
        val currentIndex = _activeTabIndex.value
        val currentTabs = _tabs.value.toMutableList()
        if (currentIndex in currentTabs.indices) {
            val current = currentTabs[currentIndex]
            val updated = current.copy(isDesktopMode = !current.isDesktopMode)
            currentTabs[currentIndex] = updated
            _tabs.value = currentTabs
            _webViewAction.value = WebViewCommand.Reload
        }
    }

    fun toggleReaderMode() {
        val tab = activeTab
        if (tab.isHomepage || tab.url == "about:blank") return

        val isCurrentlyReader = _isReaderModeOpen.value
        if (!isCurrentlyReader) {
            _readerContent.value = Pair(
                tab.title,
                "Article loaded from ${tab.url}.\n\nSakib Browser Reader Mode strips away heavy ads and distractions to provide a clean, distraction-free reading experience for students and researchers.\n\nUse the 'Ask AI Assistant' button below to summarize this webpage or translate it into Bengali!"
            )
            _isReaderModeOpen.value = true
        } else {
            _isReaderModeOpen.value = false
        }
    }

    fun setReaderContent(title: String, content: String) {
        _readerContent.value = Pair(title, content)
        _isReaderModeOpen.value = true
    }

    fun closeReaderMode() {
        _isReaderModeOpen.value = false
    }

    // WebView Callback hooks
    fun onPageStarted(url: String) {
        val index = _activeTabIndex.value
        val tabsList = _tabs.value.toMutableList()
        if (index in tabsList.indices) {
            tabsList[index] = tabsList[index].copy(
                url = url,
                isLoading = true,
                progress = 15,
                isHomepage = false
            )
            _tabs.value = tabsList
        }
    }

    fun onPageFinished(url: String, title: String?, canGoBack: Boolean, canGoForward: Boolean) {
        val index = _activeTabIndex.value
        val tabsList = _tabs.value.toMutableList()
        if (index in tabsList.indices) {
            val tab = tabsList[index]
            val safeTitle = if (!title.isNullOrBlank()) title else tab.title
            tabsList[index] = tab.copy(
                url = url,
                title = safeTitle,
                isLoading = false,
                progress = 100,
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
            _tabs.value = tabsList

            // Save to history if NOT incognito
            if (!tab.isIncognito && url != "about:blank") {
                viewModelScope.launch {
                    repository.addHistory(safeTitle, url)
                }
            }
        }
    }

    fun onProgressChanged(progress: Int) {
        val index = _activeTabIndex.value
        val tabsList = _tabs.value.toMutableList()
        if (index in tabsList.indices) {
            tabsList[index] = tabsList[index].copy(
                progress = progress,
                isLoading = progress < 100
            )
            _tabs.value = tabsList
        }
    }

    fun onTitleChanged(title: String) {
        val index = _activeTabIndex.value
        val tabsList = _tabs.value.toMutableList()
        if (index in tabsList.indices) {
            tabsList[index] = tabsList[index].copy(title = title)
            _tabs.value = tabsList
        }
    }

    // Bookmark Toggle
    fun toggleBookmarkCurrentPage() {
        val tab = activeTab
        if (tab.isHomepage || tab.url == "about:blank") return

        viewModelScope.launch {
            val exists = repository.isBookmarked(tab.url)
            if (exists) {
                repository.removeBookmark(tab.url)
            } else {
                repository.addBookmark(tab.title, tab.url)
            }
        }
    }

    fun deleteBookmark(id: Long) {
        viewModelScope.launch { repository.removeBookmarkById(id) }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch { repository.deleteHistoryById(id) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { repository.clearAllHistory() }
    }

    fun clearSearchHistory() {
        viewModelScope.launch { repository.clearSearchHistory() }
    }

    // Tutor Finder Actions
    fun setTutorCategory(category: TutorCategory) {
        _tutorCategory.value = category
        refreshTutors()
    }

    fun setTutorMode(mode: TutorMode) {
        _tutorMode.value = mode
        refreshTutors()
    }

    fun setTutorLanguage(lang: String) {
        _tutorLanguage.value = lang
        refreshTutors()
    }

    fun setTutorQuery(query: String) {
        _tutorQuery.value = query
        refreshTutors()
    }

    fun selectTutorDetail(tutor: Tutor?) {
        _selectedTutorDetail.value = tutor
    }

    private fun refreshTutors() {
        _tutorsList.value = tutorRepository.getTutors(
            category = _tutorCategory.value,
            mode = _tutorMode.value,
            searchQuery = _tutorQuery.value,
            languageFilter = _tutorLanguage.value
        )
    }

    // AI Study Assistant Actions
    fun askAi(prompt: String, contextUrlOrSnippet: String? = null) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return

        val userMsg = AiMessage(sender = MessageSender.USER, text = trimmed)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val result = GeminiClient.generateStudyHelp(trimmed, contextUrlOrSnippet)
            _isAiThinking.value = false
            result.fold(
                onSuccess = { responseText ->
                    val botMsg = AiMessage(sender = MessageSender.ASSISTANT, text = responseText)
                    _aiMessages.value = _aiMessages.value + botMsg
                },
                onFailure = { error ->
                    val errorMsg = AiMessage(
                        sender = MessageSender.ASSISTANT,
                        text = "দুঃখিত, কোনো সমস্যা হয়েছে: ${error.message ?: "অনুগ্রহ করে আবার চেষ্টা করুন।"}",
                        isError = true
                    )
                    _aiMessages.value = _aiMessages.value + errorMsg
                }
            )
        }
    }

    fun summarizeCurrentPage() {
        val tab = activeTab
        val contextSnippet = "Webpage Title: ${tab.title}\nWebpage URL: ${tab.url}"
        val prompt = "অনুগ্রহ করে এই ওয়েবপৃষ্ঠার মূল বক্তব্য ও প্রধান শিক্ষণীয় বিষয়গুলো সংক্ষেপে পরিষ্কার পয়েন্ট আকারে ব্যাখ্যা করো (Please summarize the core ideas and key educational takeaways clearly in points)."
        askAi(prompt, contextSnippet)
    }

    fun clearAiChat() {
        _aiMessages.value = listOf(
            AiMessage(
                sender = MessageSender.ASSISTANT,
                text = "চ্যাট হিস্ট্রি রিসেট করা হয়েছে। নতুন কোনো পড়াশোনা বা বিষয় নিয়ে জানতে প্রশ্ন করুন!"
            )
        )
    }

    // Settings actions
    fun setSearchEngine(engine: SearchEngine) {
        _selectedSearchEngine.value = engine
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun toggleJavaScript() {
        _javaScriptEnabled.value = !_javaScriptEnabled.value
    }

    // Dialog state setters
    fun setTabsSwitcherOpen(isOpen: Boolean) { _isTabsSwitcherOpen.value = isOpen }
    fun setBookmarksHistoryOpen(isOpen: Boolean) { _isBookmarksHistoryOpen.value = isOpen }
    fun setTutorFinderOpen(isOpen: Boolean) { _isTutorFinderOpen.value = isOpen }
    fun setAiAssistantOpen(isOpen: Boolean) { _isAiAssistantOpen.value = isOpen }
    fun setSettingsOpen(isOpen: Boolean) { _isSettingsOpen.value = isOpen }
}
