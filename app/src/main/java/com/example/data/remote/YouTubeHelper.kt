package com.example.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.regex.Pattern

object YouTubeHelper {
    private val client = OkHttpClient()

    private val VIDEO_ID_PATTERNS = listOf(
        Pattern.compile("(?:v=|/)([0-9A-Za-z_-]{11}).*"),
        Pattern.compile("youtu\\.be/([0-9A-Za-z_-]{11})"),
        Pattern.compile("youtube\\.com/embed/([0-9A-Za-z_-]{11})"),
        Pattern.compile("youtube\\.com/shorts/([0-9A-Za-z_-]{11})"),
        Pattern.compile("^([0-9A-Za-z_-]{11})$")
    )

    fun extractVideoId(urlOrId: String): String? {
        val trimmed = urlOrId.trim()
        for (pattern in VIDEO_ID_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                return matcher.group(1)
            }
        }
        return null
    }

    fun getThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    suspend fun fetchVideoDetails(videoId: String): Pair<String, String> = withContext(Dispatchers.IO) {
        val oEmbedUrl = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
        try {
            val request = Request.Builder().url(oEmbedUrl).build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val jsonStr = response.body?.string() ?: ""
                val jsonObj = JSONObject(jsonStr)
                val title = jsonObj.optString("title", "YouTube Video ($videoId)")
                val author = jsonObj.optString("author_name", "YouTube Creator")
                return@withContext Pair(title, author)
            }
        } catch (_: Exception) {
            // Fallback gracefully
        }
        Pair("YouTube Video ($videoId)", "YouTube Creator")
    }
}
