package com.example.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserPreferences
import com.example.model.RemoteState
import com.example.ui.components.BottomControlButton
import com.example.ui.components.CircularSpeedDial
import com.example.ui.components.PowerButton
import com.example.ui.components.SettingsSheet
import com.example.ui.components.TimerDialog
import com.example.ui.theme.CharcoalBackground
import com.example.ui.theme.DialBorder
import com.example.ui.theme.IndicatorLedActive
import com.example.ui.theme.IndicatorLedInactive
import com.example.ui.theme.RemoteBodyBevel
import com.example.ui.theme.RemoteBodyDark
import com.example.ui.theme.RemoteBodyGloss
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun RemoteScreen(
    uiState: RemoteState,
    preferences: UserPreferences,
    isTimerOpen: Boolean,
    isSettingsOpen: Boolean,
    onPowerClick: () -> Unit,
    onSpeedClick: (Int) -> Unit,
    onBoostClick: () -> Unit,
    onTimerClick: () -> Unit,
    onTimerDismiss: () -> Unit,
    onTimerOptionSelected: (hours: Int?) -> Unit,
    onLedClick: () -> Unit,
    onSleepClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissSettings: () -> Unit,
    onHapticChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onIndicatorChanged: (Boolean) -> Unit,
    onResetSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    BackHandler(enabled = isTimerOpen || isSettingsOpen) {
        if (isTimerOpen) onTimerDismiss()
        if (isSettingsOpen) onDismissSettings()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CharcoalBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Header
                TopHeaderBar(
                    isHardwareAvailable = uiState.isHardwareAvailable,
                    statusText = uiState.hardwareStatusText,
                    onSettingsClick = onOpenSettings
                )

                // Feedback Banner / Toast
                FeedbackBanner(
                    message = uiState.feedbackMessage,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                // Remote Physical Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLandscape) {
                        LandscapeRemoteLayout(
                            uiState = uiState,
                            onPowerClick = onPowerClick,
                            onSpeedClick = onSpeedClick,
                            onBoostClick = onBoostClick,
                            onTimerClick = onTimerClick,
                            onLedClick = onLedClick,
                            onSleepClick = onSleepClick
                        )
                    } else {
                        PortraitRemoteLayout(
                            uiState = uiState,
                            onPowerClick = onPowerClick,
                            onSpeedClick = onSpeedClick,
                            onBoostClick = onBoostClick,
                            onTimerClick = onTimerClick,
                            onLedClick = onLedClick,
                            onSleepClick = onSleepClick
                        )
                    }
                }
            }

            // Timer Dialog
            TimerDialog(
                isOpen = isTimerOpen,
                onDismiss = onTimerDismiss,
                onOptionSelected = onTimerOptionSelected
            )

            // Settings Sheet
            SettingsSheet(
                isOpen = isSettingsOpen,
                uiState = uiState,
                preferences = preferences,
                onDismiss = onDismissSettings,
                onHapticChanged = onHapticChanged,
                onSoundChanged = onSoundChanged,
                onIndicatorChanged = onIndicatorChanged,
                onResetSettings = onResetSettings
            )
        }
    }
}

/**
 * Top bar with Brand, IR status indicator pill, and Settings button.
 */
@Composable
private fun TopHeaderBar(
    isHardwareAvailable: Boolean,
    statusText: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Brand Title
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "ATOMBERG",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        // Hardware Status Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1D1F25))
                .border(0.8.dp, Color(0xFF2E303A), RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isHardwareAvailable) StatusGreen else StatusAmber)
            )
            Text(
                text = statusText,
                color = if (isHardwareAvailable) StatusGreen else StatusAmber,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Settings Button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.testTag("settings_button")
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * Animated feedback notification chip.
 */
@Composable
private fun FeedbackBanner(
    message: String?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(tween(180)) + slideInVertically(tween(180)) { -it / 2 },
        exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { -it / 2 },
        modifier = modifier
    ) {
        if (message != null) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF22242B)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF373A46)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 12.sp
                    )
                    Text(
                        text = message,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Faithful reproduction of the physical remote in Portrait mode.
 */
@Composable
private fun PortraitRemoteLayout(
    uiState: RemoteState,
    onPowerClick: () -> Unit,
    onSpeedClick: (Int) -> Unit,
    onBoostClick: () -> Unit,
    onTimerClick: () -> Unit,
    onLedClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Proportional sizing: standard physical remote ratio ~1:2.4
        val availableHeight = maxHeight
        val availableWidth = maxWidth

        val remoteWidth = minOf(330.dp, availableWidth * 0.90f)
        val remoteHeight = minOf(640.dp, availableHeight * 0.96f)

        Box(
            modifier = Modifier
                .width(remoteWidth)
                .height(remoteHeight)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(44.dp),
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .clip(RoundedCornerShape(44.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            RemoteBodyGloss,
                            RemoteBodyDark,
                            Color(0xFF141518)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = RemoteBodyBevel,
                    shape = RoundedCornerShape(44.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. TOP AREA: Aperture hole, Status LED dot, Red Power button
                TopRemoteControlArea(
                    isLedLit = uiState.isLedIndicatorLit,
                    isPowerActive = uiState.isPowerActive,
                    onPowerClick = onPowerClick,
                    modifier = Modifier.padding(top = 22.dp, start = 26.dp, end = 26.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. MAIN CIRCULAR CONTROL DIAL
                CircularSpeedDial(
                    selectedSpeed = uiState.selectedSpeed,
                    isBoostActive = uiState.isBoostActive,
                    onSpeedClick = onSpeedClick,
                    onBoostClick = onBoostClick,
                    dialDiameter = minOf(236.dp, remoteWidth * 0.74f)
                )

                Spacer(modifier = Modifier.height(26.dp))

                // 3. BOTTOM CONTROLS: TIMER, LED, SLEEP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomControlButton(
                        icon = Icons.Outlined.Timer,
                        label = "TIMER",
                        isActive = false,
                        testTag = "timer_button",
                        contentDescriptionText = "Timer",
                        onClick = onTimerClick
                    )

                    BottomControlButton(
                        icon = Icons.Outlined.WbSunny,
                        label = "LED",
                        isActive = uiState.isLedActive,
                        testTag = "led_button",
                        contentDescriptionText = "LED",
                        onClick = onLedClick
                    )

                    BottomControlButton(
                        icon = Icons.Outlined.DarkMode,
                        label = "SLEEP",
                        isActive = uiState.isSleepActive,
                        testTag = "sleep_button",
                        contentDescriptionText = "Sleep",
                        onClick = onSleepClick
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // 4. BEVEL SEAM LINE (Separates upper control face from lower matte grip)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F1012),
                                    Color(0xFF2C2E36),
                                    Color(0xFF131417)
                                )
                            )
                        )
                )

                // 5. LOWER GRIP SECTION (Ergonomic handle of physical remote)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF1A1B20),
                                    Color(0xFF131417)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle Atomberg embossed insignia at bottom of remote
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "atomberg",
                            color = Color(0x33FFFFFF),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                        Text(
                            text = "DESIGN BY SAMIM MANDAL",
                            color = Color(0x22FFFFFF),
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 1.2.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top control area with the signature physical remote elements:
 * - Left: Recessed aperture ring hole
 * - Center: Indicator status LED dot (blinks during transmission)
 * - Right: Red circular Power button
 */
@Composable
private fun TopRemoteControlArea(
    isLedLit: Boolean,
    isPowerActive: Boolean,
    onPowerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Physical remote top-left signature through-hole aperture
        Box(
            modifier = Modifier
                .size(54.dp)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color(0xFF121316))
                .border(width = 1.5.dp, color = Color(0xFF2C2E38), shape = CircleShape)
                .drawBehind {
                    // Inner depth shadow to evoke a hollow aperture through the shell
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF0B0C0E), Color(0xFF1A1B20)),
                            center = Offset(size.width * 0.45f, size.height * 0.45f),
                            radius = size.width / 2f
                        )
                    )
                }
        )

        // Center Indicator LED Dot
        val ledAlpha by animateFloatAsState(
            targetValue = if (isLedLit) 1f else 0.35f,
            animationSpec = tween(durationMillis = 100),
            label = "led_alpha"
        )
        val ledColor = if (isLedLit) IndicatorLedActive else IndicatorLedInactive

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(ledColor.copy(alpha = ledAlpha))
                .border(
                    width = 0.8.dp,
                    color = if (isLedLit) Color.White else Color(0x44FFFFFF),
                    shape = CircleShape
                )
                .drawBehind {
                    if (isLedLit) {
                        // Halo glow around LED when transmitting IR
                        drawCircle(
                            color = IndicatorLedActive.copy(alpha = 0.5f),
                            radius = size.width * 1.8f
                        )
                    }
                }
                .semantics {
                    contentDescription = if (isLedLit) "IR Transmitting" else "IR Indicator Idle"
                }
        )

        // Right: Red Power Button
        PowerButton(
            isActive = isPowerActive,
            onClick = onPowerClick
        )
    }
}

/**
 * Adaptive Landscape layout for tablets and landscape orientation.
 */
@Composable
private fun LandscapeRemoteLayout(
    uiState: RemoteState,
    onPowerClick: () -> Unit,
    onSpeedClick: (Int) -> Unit,
    onBoostClick: () -> Unit,
    onTimerClick: () -> Unit,
    onLedClick: () -> Unit,
    onSleepClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
            .widthIn(max = 700.dp)
            .shadow(16.dp, RoundedCornerShape(36.dp))
            .clip(RoundedCornerShape(36.dp))
            .background(RemoteBodyDark)
            .border(2.dp, RemoteBodyBevel, RoundedCornerShape(36.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Top controls & Bottom buttons
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopRemoteControlArea(
                isLedLit = uiState.isLedIndicatorLit,
                isPowerActive = uiState.isPowerActive,
                onPowerClick = onPowerClick,
                modifier = Modifier.width(220.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomControlButton(
                    icon = Icons.Outlined.Timer,
                    label = "TIMER",
                    isActive = false,
                    testTag = "timer_button_land",
                    contentDescriptionText = "Timer",
                    onClick = onTimerClick
                )

                BottomControlButton(
                    icon = Icons.Outlined.WbSunny,
                    label = "LED",
                    isActive = uiState.isLedActive,
                    testTag = "led_button_land",
                    contentDescriptionText = "LED",
                    onClick = onLedClick
                )

                BottomControlButton(
                    icon = Icons.Outlined.DarkMode,
                    label = "SLEEP",
                    isActive = uiState.isSleepActive,
                    testTag = "sleep_button_land",
                    contentDescriptionText = "Sleep",
                    onClick = onSleepClick
                )
            }
        }

        // Right Column: Speed Dial
        CircularSpeedDial(
            selectedSpeed = uiState.selectedSpeed,
            isBoostActive = uiState.isBoostActive,
            onSpeedClick = onSpeedClick,
            onBoostClick = onBoostClick,
            dialDiameter = 220.dp
        )
    }
}
