package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DubVoiceMode
import com.example.data.model.TranscriptSegment
import com.example.data.model.VideoDubProject
import com.example.data.remote.GeminiService
import com.example.data.repository.PresetVideo
import com.example.data.repository.TranslationRepository
import com.example.tts.DubPlaybackState
import com.example.tts.SpeakerVoiceProfile
import com.example.tts.VoiceDubEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class DubUiState(
    val inputUrl: String = "",
    val isLoading: Boolean = false,
    val loadingMessage: String = "",
    val errorMessage: String? = null,
    val currentProject: VideoDubProject? = null,
    val ultraFastMode: Boolean = true,
    val voiceMode: DubVoiceMode = DubVoiceMode.SIDE_BY_SIDE,
    val activeTab: Int = 0,
    val isRecordingMic: Boolean = false,
    val micInputText: String = "",
    val showApiKeyDialog: Boolean = false,
    val customApiKeyInput: String = ""
)

class DubViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TranslationRepository(application)
    val dubEngine = VoiceDubEngine(application)

    private val _uiState = MutableStateFlow(DubUiState())
    val uiState: StateFlow<DubUiState> = _uiState.asStateFlow()

    val playbackState: StateFlow<DubPlaybackState> = dubEngine.playbackState

    val historyProjects: StateFlow<List<VideoDubProject>> = repository.historyProjects
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val presetVideos: List<PresetVideo> = repository.getPresetVideos()

    init {
        // Preload first preset for instant experience
        viewModelScope.launch {
            val firstPreset = presetVideos.firstOrNull()
            if (firstPreset != null) {
                _uiState.value = _uiState.value.copy(inputUrl = "https://www.youtube.com/watch?v=${firstPreset.id}")
            }
        }
    }

    fun onUrlChanged(newUrl: String) {
        _uiState.value = _uiState.value.copy(inputUrl = newUrl, errorMessage = null)
    }

    fun onTabSelected(index: Int) {
        _uiState.value = _uiState.value.copy(activeTab = index)
    }

    fun setDubVoiceMode(mode: DubVoiceMode) {
        _uiState.value = _uiState.value.copy(voiceMode = mode)
    }

    fun toggleUltraFast(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(ultraFastMode = enabled)
    }

    fun translateVideo(urlOrId: String? = null) {
        val target = urlOrId ?: _uiState.value.inputUrl
        if (target.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter or paste a YouTube URL")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                loadingMessage = "Translating YouTube video speech to English-Telugu mix (Tenglish)...",
                errorMessage = null
            )
            dubEngine.stop()

            val result = repository.getOrTranslateVideo(
                urlOrId = target,
                ultraFast = _uiState.value.ultraFastMode
            )

            result.onSuccess { project ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingMessage = "",
                    currentProject = project,
                    inputUrl = project.originalUrl,
                    activeTab = 0
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingMessage = "",
                    errorMessage = error.localizedMessage ?: "Failed to translate video"
                )
            }
        }
    }

    fun selectPreset(preset: PresetVideo) {
        _uiState.value = _uiState.value.copy(inputUrl = "https://www.youtube.com/watch?v=${preset.id}")
        translateVideo(preset.id)
    }

    fun transcribeAudioSpeech(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                loadingMessage = "Transcribing voice and translating to English-Telugu mix...",
                errorMessage = null
            )
            val result = repository.transcribeAudioInput(text)
            result.onSuccess { project ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    loadingMessage = "",
                    currentProject = project,
                    activeTab = 0
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage
                )
            }
        }
    }

    fun playProjectVoice(project: VideoDubProject, startIndex: Int = 0) {
        dubEngine.playProject(project, startIndex)
    }

    fun playSingleSegment(segment: TranscriptSegment) {
        val useRoman = _uiState.value.voiceMode == DubVoiceMode.TENGLISH_ROMAN
        dubEngine.playSingleSegment(segment, useRoman)
    }

    fun pauseVoice() {
        dubEngine.pause()
    }

    fun stopVoice() {
        dubEngine.stop()
    }

    fun setVoiceProfile(profile: SpeakerVoiceProfile) {
        dubEngine.setVoiceProfile(profile)
    }

    fun updatePitchAndRate(pitch: Float, rate: Float) {
        dubEngine.updatePitchAndRate(pitch, rate)
    }

    fun toggleMuteOriginal(mute: Boolean) {
        dubEngine.toggleMuteOriginal(mute)
    }

    fun synthesizeAudioFile() {
        val proj = _uiState.value.currentProject ?: return
        dubEngine.synthesizeFullAudioFile(proj) { file ->
            // Audio file ready
        }
    }

    fun deleteHistory(videoId: String) {
        viewModelScope.launch {
            repository.deleteProject(videoId)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun openApiKeyDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            showApiKeyDialog = show,
            customApiKeyInput = GeminiService.getEffectiveApiKey()
        )
    }

    fun saveCustomApiKey(key: String) {
        GeminiService.userCustomApiKey = key.trim()
        _uiState.value = _uiState.value.copy(showApiKeyDialog = false)
    }

    override fun onCleared() {
        super.onCleared()
        dubEngine.release()
    }
}
