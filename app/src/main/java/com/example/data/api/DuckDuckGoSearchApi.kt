package com.example.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

data class InstantSearchResult(
    val title: String,
    val description: String,
    val url: String,
    val source: String? = null
)

object DuckDuckGoSearchApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun getInstantAnswers(query: String): List<InstantSearchResult> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val results = mutableListOf<InstantSearchResult>()

        try {
            val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
            val url = "https://api.duckduckgo.com/?q=$encoded&format=json&no_html=1&skip_disambig=1"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SakibBrowser/1.0 (Android)")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string() ?: ""
                val root = JSONObject(jsonStr)

                val heading = root.optString("Heading")
                val abstractText = root.optString("AbstractText")
                val abstractUrl = root.optString("AbstractURL")
                val abstractSource = root.optString("AbstractSource")

                if (abstractText.isNotBlank() && abstractUrl.isNotBlank()) {
                    results.add(
                        InstantSearchResult(
                            title = if (heading.isNotBlank()) heading else query,
                            description = abstractText,
                            url = abstractUrl,
                            source = abstractSource.ifBlank { "Instant Answer" }
                        )
                    )
                }

                // Check Related Topics
                val related = root.optJSONArray("RelatedTopics")
                if (related != null) {
                    for (i in 0 until minOf(related.length(), 6)) {
                        val item = related.optJSONObject(i) ?: continue
                        val text = item.optString("Text")
                        val firstUrl = item.optString("FirstURL")
                        if (text.isNotBlank() && firstUrl.isNotBlank()) {
                            // Extract title before "-" or ":"
                            val title = if (text.contains(" - ")) {
                                text.substringBefore(" - ")
                            } else text.take(60)

                            results.add(
                                InstantSearchResult(
                                    title = title,
                                    description = text,
                                    url = firstUrl,
                                    source = "Related Result"
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Gracefully ignore network errors on suggestions/instant answers
        }

        results
    }

    suspend fun getSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        if (query.isBlank() || query.length < 2) return@withContext emptyList()
        val suggestions = mutableListOf<String>()

        try {
            val encoded = URLEncoder.encode(query, StandardCharsets.UTF_8.name())
            val url = "https://duckduckgo.com/ac/?q=$encoded&type=list"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "SakibBrowser/1.0 (Android)")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string() ?: ""
                val array = JSONArray(jsonStr)
                if (array.length() >= 2) {
                    val list = array.optJSONArray(1)
                    if (list != null) {
                        for (i in 0 until minOf(list.length(), 6)) {
                            val item = list.optString(i)
                            if (item.isNotBlank()) suggestions.add(item)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore suggestion errors
        }

        suggestions
    }
}
