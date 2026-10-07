package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
import com.example.model.FanProfile
import com.example.model.RemoteState
import com.example.ui.theme.DialBorder
import com.example.ui.theme.PowerRed
import com.example.ui.theme.RemoteBodyDark
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    isOpen: Boolean,
    uiState: RemoteState,
    preferences: UserPreferences,
    onDismiss: () -> Unit,
    onHapticChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onIndicatorChanged: (Boolean) -> Unit,
    onResetSettings: () -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = RemoteBodyDark,
        scrimColor = Color.Black.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Remote Settings",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Section 1: IR Hardware Status
            SectionCard(title = "IR Hardware Status") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isHardwareAvailable) StatusGreen else StatusAmber)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (uiState.isHardwareAvailable) "IR Blaster Ready" else "No Built-in IR Blaster",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.hardwareDetails.ifEmpty {
                                if (uiState.isHardwareAvailable) "Ready to transmit 38 kHz signals"
                                else "This device cannot emit physical infrared signals"
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Section 2: Fan Profile & IR Table
            SectionCard(title = "Fan Profile") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.activeProfile.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "38 kHz NEC",
                            color = Color(0xFF64B5F6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = uiState.activeProfile.description,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(color = Color(0x1AFFFFFF), thickness = 0.5.dp)

                    // Hex code mapping breakdown
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF131417))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        HexRow("Power", "0x6E91F300")
                        HexRow("Speed 1", "0x748BF300")
                        HexRow("Speed 2", "0x6F90F300")
                        HexRow("Speed 3", "0x758AF300")
                        HexRow("Speed 4", "0x6C93F300")
                        HexRow("Speed 5", "0x7788F300")
                        HexRow("Boost", "0x708FF300")
                        HexRow("Timer", "0x6996F300")
                        HexRow("Sleep", "0x718EF300")
                        HexRow("LED", "NOT CONFIGURED", isWarning = true)
                    }
                }
            }

            // Section 3: Tactile & Feedback Controls
            SectionCard(title = "Feedback & Preferences") {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SettingToggleRow(
                        icon = Icons.Outlined.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Subtle click vibration on button press",
                        checked = preferences.hapticFeedbackEnabled,
                        onCheckedChange = onHapticChanged,
                        testTag = "setting_haptics_switch"
                    )

                    SettingToggleRow(
                        icon = Icons.Outlined.VolumeUp,
                        title = "Button Click Sound",
                        subtitle = "Audio tick on button tap",
                        checked = preferences.buttonSoundEnabled,
                        onCheckedChange = onSoundChanged,
                        testTag = "setting_sound_switch"
                    )

                    SettingToggleRow(
                        icon = Icons.Outlined.Lightbulb,
                        title = "LED Indicator Pulse",
                        subtitle = "Flash top remote LED during transmission",
                        checked = preferences.indicatorAnimationEnabled,
                        onCheckedChange = onIndicatorChanged,
                        testTag = "setting_led_switch"
                    )
                }
            }

            // Section 4: Reset
            OutlinedButton(
                onClick = onResetSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_settings_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reset Settings to Default",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            // Section 5: About Screen (clean & minimal per spec 33)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Atomberg",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Smart IR Remote Controller",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "Offline-First • Version 1.0",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1B1C22))
            .border(width = 0.8.dp, color = DialBorder, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            color = TextTertiary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
        content()
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag),
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PowerRed,
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = Color(0xFF282A33)
            )
        )
    }
}

@Composable
private fun HexRow(name: String, code: String, isWarning: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = code,
            color = if (isWarning) StatusAmber else Color(0xFF81C784),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
