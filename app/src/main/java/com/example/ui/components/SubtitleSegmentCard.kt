package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DubVoiceMode
import com.example.data.model.TranscriptSegment
import com.example.ui.theme.MixPillBg
import com.example.ui.theme.MixPillText
import com.example.ui.theme.TeluguPillBg
import com.example.ui.theme.TeluguPillText

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubtitleSegmentCard(
    segment: TranscriptSegment,
    isActive: Boolean,
    voiceMode: DubVoiceMode,
    onPlayClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "border_color"
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
        label = "card_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("subtitle_segment_${segment.id}")
            .clickable { onPlayClicked() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(if (isActive) 2.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Timestamp + Dubbed Voice Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = segment.durationFormatted,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isActive) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DUBBING LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MixPillBg.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Tenglish Mix",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                FilledTonalIconButton(
                    onClick = onPlayClicked,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("play_segment_${segment.id}"),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isActive) Color.White else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.PlayArrow,
                        contentDescription = "Play voice dub for segment ${segment.id}",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // English-Telugu Mix in Telugu Script
            if (voiceMode == DubVoiceMode.TELUGU_SCRIPT || voiceMode == DubVoiceMode.SIDE_BY_SIDE) {
                Text(
                    text = segment.teluguScript,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // English-Telugu Mix in Romanized / English Letters
            if (voiceMode == DubVoiceMode.TENGLISH_ROMAN || voiceMode == DubVoiceMode.SIDE_BY_SIDE) {
                if (voiceMode == DubVoiceMode.SIDE_BY_SIDE) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
                Text(
                    text = segment.tenglishRoman,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 20.sp
                    ),
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Original English Spoken Voice (Reference)
            if (voiceMode == DubVoiceMode.SIDE_BY_SIDE || voiceMode == DubVoiceMode.ORIGINAL_ENGLISH) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Original: ",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = segment.originalText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Vocabulary / Glossary Chips
            if (segment.keyTerms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    segment.keyTerms.forEach { term ->
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "${term.englishWord}: ${term.teluguMeaning}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            ),
                            border = null,
                            modifier = Modifier.height(26.dp)
                        )
                    }
                }
            }
        }
    }
}
