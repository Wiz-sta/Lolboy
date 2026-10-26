package com.example.data.repository

import com.example.data.db.BookmarkEntity
import com.example.data.db.BrowserDao
import com.example.data.db.HistoryEntity
import com.example.data.db.SavedArticleEntity
import com.example.data.db.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {

    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val allHistory: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val recentSearches: Flow<List<SearchHistoryEntity>> = dao.getRecentSearches()
    val savedArticles: Flow<List<SavedArticleEntity>> = dao.getAllSavedArticles()

    suspend fun isBookmarked(url: String): Boolean {
        return dao.isBookmarked(url)
    }

    suspend fun addBookmark(title: String, url: String, faviconUrl: String? = null) {
        dao.insertBookmark(BookmarkEntity(title = title.ifBlank { url }, url = url, faviconUrl = faviconUrl))
    }

    suspend fun removeBookmark(url: String) {
        dao.deleteBookmarkByUrl(url)
    }

    suspend fun removeBookmarkById(id: Long) {
        dao.deleteBookmarkById(id)
    }

    suspend fun addHistory(title: String, url: String) {
        if (url == "about:blank" || url.isBlank()) return
        dao.insertHistory(HistoryEntity(title = title.ifBlank { url }, url = url))
    }

    suspend fun deleteHistoryById(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAllHistory() {
        dao.clearAllHistory()
    }

    suspend fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        dao.deleteSearchQuery(query) // remove duplicate to put at top
        dao.insertSearchQuery(SearchHistoryEntity(query = query))
    }

    suspend fun deleteSearchQuery(query: String) {
        dao.deleteSearchQuery(query)
    }

    suspend fun clearSearchHistory() {
        dao.clearSearchHistory()
    }

    suspend fun saveArticle(title: String, url: String, contentText: String) {
        dao.insertSavedArticle(
            SavedArticleEntity(
                title = title.ifBlank { url },
                url = url,
                contentText = contentText
            )
        )
    }

    suspend fun deleteSavedArticle(id: Long) {
        dao.deleteSavedArticleById(id)
    }
}
