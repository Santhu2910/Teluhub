package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.JsonMapper
import com.example.data.model.VideoDubProject
import com.example.data.remote.GeminiService
import com.example.data.remote.YouTubeHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TranslationRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.dubHistoryDao()

    val historyProjects: Flow<List<VideoDubProject>> = dao.getAllHistory().map { list ->
        list.map { JsonMapper.entityToProject(it) }
    }

    suspend fun getOrTranslateVideo(
        urlOrId: String,
        ultraFast: Boolean = true
    ): Result<VideoDubProject> {
        val videoId = YouTubeHelper.extractVideoId(urlOrId)
            ?: return Result.failure(IllegalArgumentException("Invalid YouTube URL or Video ID. Please check the link."))

        // 1. Check local cache
        val cached = dao.getProjectById(videoId)
        if (cached != null) {
            return Result.success(JsonMapper.entityToProject(cached))
        }

        // 2. Fetch title and author
        val (title, author) = YouTubeHelper.fetchVideoDetails(videoId)

        // 3. Translate using Gemini / Smart Telugu Mix Engine
        val project = GeminiService.translateVideoToTenglish(
            videoId = videoId,
            videoTitle = title,
            channelName = author,
            ultraFast = ultraFast
        )

        // 4. Cache in Room DB
        dao.insertProject(JsonMapper.projectToEntity(project))

        return Result.success(project)
    }

    suspend fun transcribeAudioInput(
        audioText: String,
        audioBase64: String? = null
    ): Result<VideoDubProject> {
        return try {
            val project = GeminiService.transcribeAudioToTenglish(audioText, audioBase64)
            dao.insertProject(JsonMapper.projectToEntity(project))
            Result.success(project)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProject(videoId: String) {
        dao.deleteProject(videoId)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }

    fun getPresetVideos(): List<PresetVideo> {
        return listOf(
            PresetVideo(
                id = "dQw4w9WgXcQ",
                title = "Rick Astley - Never Gonna Give You Up",
                category = "Music / Classic",
                author = "RickAstleyVEVO",
                duration = "3:32"
            ),
            PresetVideo(
                id = "k1BneeJTDcU",
                title = "Smartphone Innovation Review - Flagship Camera & Specs",
                category = "Tech Review",
                author = "Tech Explorers",
                duration = "8:45"
            ),
            PresetVideo(
                id = "y881t8ilMyc",
                title = "How Black Holes Warp Time and Space - Deep Cosmos",
                category = "Science",
                author = "Science Channel",
                duration = "10:12"
            ),
            PresetVideo(
                id = "UF8uR6Z6KLc",
                title = "Steve Jobs 2005 Stanford Speech - Stay Hungry Stay Foolish",
                category = "Motivation",
                author = "Stanford",
                duration = "15:04"
            ),
            PresetVideo(
                id = "zwwy3Z32RZs",
                title = "10 Minute Authentic Hyderabadi Cooking Tutorial",
                category = "Cooking & Food",
                author = "Kitchen Masters",
                duration = "10:20"
            )
        )
    }
}

data class PresetVideo(
    val id: String,
    val title: String,
    val category: String,
    val author: String,
    val duration: String
) {
    val thumbnailUrl: String get() = YouTubeHelper.getThumbnailUrl(id)
}
