package com.example.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AsyncImage
import com.example.data.model.DubVoiceMode
import com.example.data.model.VideoDubProject
import com.example.data.repository.PresetVideo
import com.example.ui.components.SubtitleSegmentCard
import com.example.ui.components.VoiceCloningPanel
import com.example.ui.components.YouTubePlayerView
import com.example.ui.theme.MixPillBg
import com.example.ui.theme.RedPrimary
import com.example.ui.viewmodel.DubViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DubViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RedPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TeluTube Dub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "YouTube Video to English-Telugu Mix",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openApiKeyDialog(true) },
                        modifier = Modifier.testTag("api_key_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "API Key",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Navigation Tabs
            TabRow(
                selectedTabIndex = uiState.activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.onTabSelected(0) },
                    text = { Text("Video Dub", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_video_dub")
                )
                Tab(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.onTabSelected(1) },
                    text = { Text("Transcribe Audio", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_transcribe_audio")
                )
                Tab(
                    selected = uiState.activeTab == 2,
                    onClick = { viewModel.onTabSelected(2) },
                    text = { Text("Web Edition", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_web_edition")
                )
                Tab(
                    selected = uiState.activeTab == 3,
                    onClick = { viewModel.onTabSelected(3) },
                    text = { Text("History", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_history")
                )
            }

            when (uiState.activeTab) {
                0 -> VideoDubTabContent(
                    viewModel = viewModel,
                    uiState = uiState,
                    playbackState = playbackState,
                    context = context
                )
                1 -> AudioTranscribeTabContent(viewModel = viewModel)
                2 -> WebEditionTabContent()
                3 -> HistoryTabContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WebEditionTabContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val webView = remember {
        android.webkit.WebView(context).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
            }
            webChromeClient = android.webkit.WebChromeClient()
            webViewClient = android.webkit.WebViewClient()
            loadUrl("file:///android_asset/web/index.html")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView.stopLoading()
        }
    }

    androidx.compose.ui.viewinterop.AndroidView<android.webkit.WebView>(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun VideoDubTabContent(
    viewModel: DubViewModel,
    uiState: com.example.ui.viewmodel.DubUiState,
    playbackState: com.example.tts.DubPlaybackState,
    context: Context
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. YouTube Link Input Section
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enter YouTube Video Link",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // Ultra-fast turbo mode toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Very Fast Mode",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = uiState.ultraFastMode,
                                onCheckedChange = { viewModel.toggleUltraFast(it) },
                                modifier = Modifier.size(width = 38.dp, height = 24.dp),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFF59E0B)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.inputUrl,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        placeholder = { Text("https://www.youtube.com/watch?v=... or Video ID", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("youtube_url_input"),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (uiState.inputUrl.isNotBlank()) {
                                    IconButton(onClick = { viewModel.onUrlChanged("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear input")
                                    }
                                }
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = clipboard.primaryClip
                                    if (clip != null && clip.itemCount > 0) {
                                        val text = clip.getItemAt(0).text?.toString() ?: ""
                                        viewModel.onUrlChanged(text)
                                    }
                                }) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = "Paste from clipboard")
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.translateVideo() },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("translate_very_fast_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(uiState.loadingMessage.ifBlank { "Translating very fast..." }, fontSize = 13.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFFDE047)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Translate Voice to English-Telugu Mix",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // 2. Sample Presets (Fast 1-Tap Load)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant 1-Tap Sample Videos:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Zero-wait Demo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel.presetVideos) { preset ->
                        PresetCard(
                            preset = preset,
                            onClick = { viewModel.selectPreset(preset) }
                        )
                    }
                }
            }
        }

        // 3. Translated Project Viewer (Player + Voice Cloning + Segments)
        val project = uiState.currentProject
        if (project != null) {
            item {
                ActiveProjectSection(
                    project = project,
                    playbackState = playbackState,
                    voiceMode = uiState.voiceMode,
                    onVoiceModeChange = { viewModel.setDubVoiceMode(it) },
                    onPlayProject = { viewModel.playProjectVoice(project) },
                    onPauseProject = { viewModel.pauseVoice() },
                    onPlaySegment = { viewModel.playSingleSegment(it) },
                    onProfileSelected = { viewModel.setVoiceProfile(it) },
                    onPitchRateChanged = { p, r -> viewModel.updatePitchAndRate(p, r) },
                    onSynthesizeAudio = { viewModel.synthesizeAudioFile() },
                    onToggleMute = { viewModel.toggleMuteOriginal(it) }
                )
            }

            // Subtitle Segments List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Translated Voice Lines (${project.segments.size} Segments)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any card to speak or seek",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Script View Mode Filter
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterChip(
                            selected = uiState.voiceMode == DubVoiceMode.SIDE_BY_SIDE,
                            onClick = { viewModel.setDubVoiceMode(DubVoiceMode.SIDE_BY_SIDE) },
                            label = { Text("All", fontSize = 11.sp) },
                            modifier = Modifier.height(30.dp)
                        )
                        FilterChip(
                            selected = uiState.voiceMode == DubVoiceMode.TELUGU_SCRIPT,
                            onClick = { viewModel.setDubVoiceMode(DubVoiceMode.TELUGU_SCRIPT) },
                            label = { Text("తెలుగు", fontSize = 11.sp) },
                            modifier = Modifier.height(30.dp)
                        )
                        FilterChip(
                            selected = uiState.voiceMode == DubVoiceMode.TENGLISH_ROMAN,
                            onClick = { viewModel.setDubVoiceMode(DubVoiceMode.TENGLISH_ROMAN) },
                            label = { Text("Tenglish", fontSize = 11.sp) },
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }

            // Segments
            items(project.segments) { segment ->
                val isActive = playbackState.isPlaying && playbackState.activeSegmentId == segment.id
                SubtitleSegmentCard(
                    segment = segment,
                    isActive = isActive,
                    voiceMode = uiState.voiceMode,
                    onPlayClicked = { viewModel.playSingleSegment(segment) }
                )
            }
        }
    }
}

@Composable
fun PresetCard(
    preset: PresetVideo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("preset_video_${preset.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                AsyncImage(
                    model = preset.thumbnailUrl,
                    contentDescription = preset.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = preset.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = preset.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = preset.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun ActiveProjectSection(
    project: VideoDubProject,
    playbackState: com.example.tts.DubPlaybackState,
    voiceMode: DubVoiceMode,
    onVoiceModeChange: (DubVoiceMode) -> Unit,
    onPlayProject: () -> Unit,
    onPauseProject: () -> Unit,
    onPlaySegment: (com.example.data.model.TranscriptSegment) -> Unit,
    onProfileSelected: (com.example.tts.SpeakerVoiceProfile) -> Unit,
    onPitchRateChanged: (Float, Float) -> Unit,
    onSynthesizeAudio: () -> Unit,
    onToggleMute: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Embedded YouTube Player
        YouTubePlayerView(
            videoId = project.videoId,
            isMuted = playbackState.muteOriginalVideo
        )

        // Video Info & Summary Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = project.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Channel: ${project.channelTitle} • ${project.durationSeconds}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tenglish summary
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MixPillBg.copy(alpha = 0.1f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "English-Telugu Mix Overview:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = project.summaryTeluguMix,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Voice Dubbing Playback Controls Bar
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (playbackState.isPlaying) onPauseProject() else onPlayProject()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("play_full_dub_btn")
                    ) {
                        Icon(
                            imageVector = if (playbackState.isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                            contentDescription = if (playbackState.isPlaying) "Pause" else "Play Full Dub"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (playbackState.isPlaying) "Pause Voice" else "Play Dubbed Voice",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (playbackState.isPlaying) {
                        Text(
                            text = "Playing...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mute original video toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Mute Video",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = playbackState.muteOriginalVideo,
                        onCheckedChange = { onToggleMute(it) },
                        modifier = Modifier.size(width = 38.dp, height = 24.dp)
                    )
                }
            }
        }

        // Voice Cloning & Audio File Export Panel
        VoiceCloningPanel(
            project = project,
            playbackState = playbackState,
            onProfileSelected = onProfileSelected,
            onPitchRateChanged = onPitchRateChanged,
            onSynthesizeAudioFile = onSynthesizeAudio
        )
    }
}
