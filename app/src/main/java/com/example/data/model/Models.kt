package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranscriptSegment(
    val id: Int,
    val startSeconds: Float,
    val endSeconds: Float,
    val originalText: String,
    val teluguScript: String,
    val tenglishRoman: String,
    val keyTerms: List<GlossaryTerm> = emptyList()
) {
    val durationFormatted: String
        get() {
            val startMin = (startSeconds / 60).toInt()
            val startSec = (startSeconds % 60).toInt()
            return String.format("%02d:%02d", startMin, startSec)
        }
}

@JsonClass(generateAdapter = true)
data class GlossaryTerm(
    val englishWord: String,
    val teluguMeaning: String,
    val context: String = ""
)

@JsonClass(generateAdapter = true)
data class VideoDubProject(
    val videoId: String,
    val originalUrl: String,
    val title: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val segments: List<TranscriptSegment>,
    val summaryOriginal: String,
    val summaryTeluguMix: String,
    val summaryTeluguScript: String,
    val translationSpeed: String = "Fast",
    val dateCreated: Long = System.currentTimeMillis()
)

data class AudioTranscriptionResult(
    val id: String,
    val durationMs: Long,
    val originalTranscript: String,
    val detectedLanguage: String,
    val teluguMixScript: String,
    val teluguMixRoman: String,
    val segments: List<TranscriptSegment>
)

enum class DubVoiceMode {
    TELUGU_SCRIPT,
    TENGLISH_ROMAN,
    SIDE_BY_SIDE,
    ORIGINAL_ENGLISH
}
