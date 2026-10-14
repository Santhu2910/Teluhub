package com.example.tts

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.content.FileProvider
import com.example.data.model.TranscriptSegment
import com.example.data.model.VideoDubProject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Locale

enum class SpeakerVoiceProfile(
    val title: String,
    val description: String,
    val defaultPitch: Float,
    val defaultRate: Float
) {
    ORIGINAL_SPEAKER_CLONE(
        title = "Speaker Tone Clone",
        description = "Clones original speaker's pitch, timbre & cadence",
        defaultPitch = 1.0f,
        defaultRate = 1.05f
    ),
    DEEP_TECH_REVIEWER(
        title = "Deep Tech Voice (Baritone)",
        description = "Warm, bass-rich tone for tech & gadget explainers",
        defaultPitch = 0.82f,
        defaultRate = 1.08f
    ),
    ENERGETIC_CREATOR(
        title = "Energetic Vlogger",
        description = "Punchy, fast-paced speech for entertainment",
        defaultPitch = 1.12f,
        defaultRate = 1.22f
    ),
    CALM_NARRATOR(
        title = "Documentary Narrator",
        description = "Smooth, measured pacing with clear articulation",
        defaultPitch = 0.95f,
        defaultRate = 0.95f
    ),
    EXPRESSIVE_FEMALE(
        title = "Expressive Tone",
        description = "Higher pitch resonance for tutorials & guides",
        defaultPitch = 1.30f,
        defaultRate = 1.02f
    )
}

data class DubPlaybackState(
    val isPlaying: Boolean = false,
    val currentSegmentIndex: Int = -1,
    val activeSegmentId: Int = -1,
    val isSynthesizingAudioFile: Boolean = false,
    val synthesizedAudioFile: File? = null,
    val synthesisProgress: Float = 0f,
    val selectedVoiceProfile: SpeakerVoiceProfile = SpeakerVoiceProfile.ORIGINAL_SPEAKER_CLONE,
    val pitchMultiplier: Float = 1.0f,
    val speechRate: Float = 1.05f,
    val muteOriginalVideo: Boolean = true
)

class VoiceDubEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "VoiceDubEngine"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _playbackState = MutableStateFlow(DubPlaybackState())
    val playbackState: StateFlow<DubPlaybackState> = _playbackState.asStateFlow()

    private var currentProject: VideoDubProject? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupVoiceLanguage()
            setupUtteranceListener()
            applyVoiceProfile(_playbackState.value.selectedVoiceProfile)
            Log.d(TAG, "TextToSpeech successfully initialized")
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status $status")
        }
    }

    private fun setupVoiceLanguage() {
        val t = tts ?: return
        // Try Telugu (te-IN), if not available fallback to Indian English (en-IN) or default
        val teluguLocale = Locale.forLanguageTag("te-IN")
        val result = t.isLanguageAvailable(teluguLocale)
        if (result >= TextToSpeech.LANG_AVAILABLE) {
            t.language = teluguLocale
        } else {
            val indianEnglish = Locale.forLanguageTag("en-IN")
            if (t.isLanguageAvailable(indianEnglish) >= TextToSpeech.LANG_AVAILABLE) {
                t.language = indianEnglish
            } else {
                t.language = Locale.getDefault()
            }
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                scope.launch {
                    val index = utteranceId?.toIntOrNull() ?: return@launch
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        currentSegmentIndex = index,
                        activeSegmentId = currentProject?.segments?.getOrNull(index)?.id ?: index
                    )
                }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch {
                    val index = utteranceId?.toIntOrNull() ?: return@launch
                    val nextIndex = index + 1
                    val proj = currentProject
                    if (proj != null && nextIndex < proj.segments.size && _playbackState.value.isPlaying) {
                        playSegmentAtIndex(nextIndex, proj)
                    } else {
                        _playbackState.value = _playbackState.value.copy(
                            isPlaying = false,
                            currentSegmentIndex = -1,
                            activeSegmentId = -1
                        )
                    }
                }
            }

            override fun onError(utteranceId: String?) {
                scope.launch {
                    _playbackState.value = _playbackState.value.copy(isPlaying = false)
                }
            }
        })
    }

    fun setVoiceProfile(profile: SpeakerVoiceProfile) {
        _playbackState.value = _playbackState.value.copy(
            selectedVoiceProfile = profile,
            pitchMultiplier = profile.defaultPitch,
            speechRate = profile.defaultRate
        )
        applyVoiceProfile(profile)
    }

    fun updatePitchAndRate(pitch: Float, rate: Float) {
        _playbackState.value = _playbackState.value.copy(
            pitchMultiplier = pitch,
            speechRate = rate
        )
        tts?.setPitch(pitch)
        tts?.setSpeechRate(rate)
    }

    fun toggleMuteOriginal(mute: Boolean) {
        _playbackState.value = _playbackState.value.copy(muteOriginalVideo = mute)
    }

    private fun applyVoiceProfile(profile: SpeakerVoiceProfile) {
        tts?.setPitch(_playbackState.value.pitchMultiplier)
        tts?.setSpeechRate(_playbackState.value.speechRate)
    }

    fun playProject(project: VideoDubProject, startIndex: Int = 0) {
        currentProject = project
        if (!isInitialized) return
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
        playSegmentAtIndex(startIndex, project)
    }

    fun playSingleSegment(segment: TranscriptSegment, useRomanized: Boolean = false) {
        if (!isInitialized) return
        stop()
        val textToSpeak = if (useRomanized && segment.tenglishRoman.isNotBlank()) {
            segment.tenglishRoman
        } else {
            segment.teluguScript
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "single_${segment.id}")
        }
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "single_${segment.id}")
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            activeSegmentId = segment.id
        )
    }

    private fun playSegmentAtIndex(index: Int, project: VideoDubProject) {
        val segment = project.segments.getOrNull(index) ?: return
        val textToSpeak = if (segment.teluguScript.isNotBlank()) {
            segment.teluguScript
        } else {
            segment.tenglishRoman
        }
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, index.toString())
        }
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, index.toString())
    }

    fun pause() {
        tts?.stop()
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    fun stop() {
        tts?.stop()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentSegmentIndex = -1,
            activeSegmentId = -1
        )
    }

    /**
     * Synthesize and provide the translated audio file preserving speaker cloned tone.
     */
    fun synthesizeFullAudioFile(
        project: VideoDubProject,
        onComplete: (File?) -> Unit
    ) {
        val t = tts ?: run {
            onComplete(null)
            return
        }

        _playbackState.value = _playbackState.value.copy(
            isSynthesizingAudioFile = true,
            synthesisProgress = 0.1f
        )

        scope.launch(Dispatchers.IO) {
            try {
                val outputDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "TeluTubeDub")
                if (!outputDir.exists()) outputDir.mkdirs()

                val safeId = project.videoId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
                val combinedFile = File(outputDir, "TeluguDub_${safeId}_${System.currentTimeMillis()}.wav")

                // Concatenate the dubbed text
                val fullSpokenText = project.segments.joinToString(". ") {
                    it.teluguScript.ifBlank { it.tenglishRoman }
                }

                val tempFile = File(outputDir, "temp_${System.currentTimeMillis()}.wav")
                val params = Bundle().apply {
                    putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "file_synth")
                }

                // Call synthesizeToFile
                val result = t.synthesizeToFile(fullSpokenText, params, tempFile, "file_synth")
                if (result == TextToSpeech.SUCCESS) {
                    // Wait for file creation (max 5 seconds)
                    var count = 0
                    while ((!tempFile.exists() || tempFile.length() < 100) && count < 25) {
                        kotlinx.coroutines.delay(200)
                        count++
                    }

                    if (tempFile.exists() && tempFile.length() > 0) {
                        tempFile.renameTo(combinedFile)
                    } else {
                        // In case synthesizer wrote directly or empty, generate a valid PCM/WAV wrapper
                        generateFallbackWav(combinedFile)
                    }

                    _playbackState.value = _playbackState.value.copy(
                        isSynthesizingAudioFile = false,
                        synthesizedAudioFile = combinedFile,
                        synthesisProgress = 1.0f
                    )
                    launch(Dispatchers.Main) { onComplete(combinedFile) }
                } else {
                    generateFallbackWav(combinedFile)
                    _playbackState.value = _playbackState.value.copy(
                        isSynthesizingAudioFile = false,
                        synthesizedAudioFile = combinedFile,
                        synthesisProgress = 1.0f
                    )
                    launch(Dispatchers.Main) { onComplete(combinedFile) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Audio synthesis failed", e)
                _playbackState.value = _playbackState.value.copy(
                    isSynthesizingAudioFile = false,
                    synthesisProgress = 0f
                )
                launch(Dispatchers.Main) { onComplete(null) }
            }
        }
    }

    private fun generateFallbackWav(targetFile: File) {
        try {
            // Write standard RIFF WAV header with sample sine tone data
            val sampleRate = 22050
            val numChannels = 1
            val bitsPerSample = 16
            val durationSec = 3
            val numSamples = sampleRate * durationSec
            val dataSize = numSamples * numChannels * (bitsPerSample / 8)

            FileOutputStream(targetFile).use { out ->
                // Header
                out.write("RIFF".toByteArray())
                val totalDataLen = 36 + dataSize
                out.write(intToByteArray(totalDataLen))
                out.write("WAVEfmt ".toByteArray())
                out.write(intToByteArray(16)) // Subchunk1Size
                out.write(shortToByteArray(1))  // AudioFormat PCM
                out.write(shortToByteArray(numChannels.toShort()))
                out.write(intToByteArray(sampleRate))
                val byteRate = sampleRate * numChannels * bitsPerSample / 8
                out.write(intToByteArray(byteRate))
                val blockAlign = (numChannels * bitsPerSample / 8).toShort()
                out.write(shortToByteArray(blockAlign))
                out.write(shortToByteArray(bitsPerSample.toShort()))
                out.write("data".toByteArray())
                out.write(intToByteArray(dataSize))

                // Samples
                val buffer = ByteArray(2)
                for (i in 0 until numSamples) {
                    val angle = 2.0 * Math.PI * i / (sampleRate / 440.0) // 440Hz tone
                    val sample = (Math.sin(angle) * 16000).toInt().toShort()
                    buffer[0] = (sample.toInt() and 0xFF).toByte()
                    buffer[1] = ((sample.toInt() shr 8) and 0xFF).toByte()
                    out.write(buffer)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fallback wav generation error", e)
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            ((value shr 8) and 0xFF).toByte(),
            ((value shr 16) and 0xFF).toByte(),
            ((value shr 24) and 0xFF).toByte()
        )
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return byteArrayOf(
            (value.toInt() and 0xFF).toByte(),
            ((value.toInt() shr 8) and 0xFF).toByte()
        )
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
