package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.RemoteScreen
import com.example.ui.theme.AtombergTheme
import com.example.viewmodel.RemoteViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AtombergTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh hardware status in case permissions or hardware state changed
        viewModel.refreshHardwareStatus()
    }
}

@Composable
fun MainContent(viewModel: RemoteViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val isTimerOpen by viewModel.isTimerDialogVisible.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsSheetVisible.collectAsStateWithLifecycle()

    RemoteScreen(
        uiState = uiState,
        preferences = preferences,
        isTimerOpen = isTimerOpen,
        isSettingsOpen = isSettingsOpen,
        onPowerClick = viewModel::onPowerClicked,
        onSpeedClick = viewModel::onSpeedClicked,
        onBoostClick = viewModel::onBoostClicked,
        onTimerClick = viewModel::onTimerClicked,
        onTimerDismiss = viewModel::dismissTimerDialog,
        onTimerOptionSelected = viewModel::onTimerOptionSelected,
        onLedClick = viewModel::onLedClicked,
        onSleepClick = viewModel::onSleepClicked,
        onOpenSettings = viewModel::openSettings,
        onDismissSettings = viewModel::dismissSettings,
        onHapticChanged = viewModel::setHapticFeedback,
        onSoundChanged = viewModel::setButtonSound,
        onIndicatorChanged = viewModel::setIndicatorAnimation,
        onResetSettings = viewModel::resetSettings
    )
}
