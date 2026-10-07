package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PreferencesRepository
import com.example.data.UserPreferences
import com.example.ir.AtombergIrCommands
import com.example.ir.AtombergIrController
import com.example.ir.AtombergIrProtocol
import com.example.ir.IrHardwareDetector
import com.example.ir.IrHardwareStatus
import com.example.ir.TransmissionResult
import com.example.model.FanProfile
import com.example.model.RemoteState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val hardwareDetector = IrHardwareDetector(application)
    private val irController = AtombergIrController(hardwareDetector)
    private val preferencesRepository = PreferencesRepository(application)

    private val _uiState = MutableStateFlow(RemoteState())
    val uiState: StateFlow<RemoteState> = _uiState.asStateFlow()

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferences

    // Dialog & bottom sheet visibility
    private val _isTimerDialogVisible = MutableStateFlow(false)
    val isTimerDialogVisible: StateFlow<Boolean> = _isTimerDialogVisible.asStateFlow()

    private val _isSettingsSheetVisible = MutableStateFlow(false)
    val isSettingsSheetVisible: StateFlow<Boolean> = _isSettingsSheetVisible.asStateFlow()

    private var ledBlinkJob: Job? = null
    private var bannerClearJob: Job? = null

    private val audioManager = application.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        refreshHardwareStatus()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferences.collect { prefs ->
                irController.protocol = irController.protocol.copy(
                    carrierFrequencyHz = prefs.carrierFrequencyHz
                )
            }
        }
    }

    fun refreshHardwareStatus() {
        when (val status = hardwareDetector.checkHardwareStatus()) {
            is IrHardwareStatus.Available -> {
                val details = if (status.carrierRanges.isEmpty()) {
                    "Emitter active (Standard 38kHz)"
                } else {
                    "Emitter active: " + status.carrierRanges.joinToString(", ")
                }
                _uiState.update {
                    it.copy(
                        isHardwareAvailable = true,
                        hardwareStatusText = "IR Ready",
                        hardwareDetails = details
                    )
                }
            }
            is IrHardwareStatus.NotAvailable -> {
                _uiState.update {
                    it.copy(
                        isHardwareAvailable = false,
                        hardwareStatusText = "No IR Blaster",
                        hardwareDetails = status.reason
                    )
                }
            }
        }
    }

    /**
     * Executes IR transmission with audio, haptic, and visual telemetry feedback.
     */
    private fun sendCommand(
        commandName: String,
        commandHex: Long?,
        onSuccessStateUpdate: () -> Unit
    ) {
        triggerHapticFeedback()
        triggerSoundFeedback()

        val result = irController.transmitCommand(commandName, commandHex)

        triggerLedBlink()

        val now = System.currentTimeMillis()

        when (result) {
            is TransmissionResult.Success -> {
                onSuccessStateUpdate()
                _uiState.update {
                    it.copy(
                        lastCommandName = result.commandName,
                        lastCommandHex = result.hexString,
                        lastSentTimestamp = now,
                        feedbackMessage = "Sent: ${result.commandName} (${result.hexString})"
                    )
                }
                scheduleBannerClear()
            }
            is TransmissionResult.HardwareUnavailable -> {
                _uiState.update {
                    it.copy(
                        feedbackMessage = result.reason
                    )
                }
                scheduleBannerClear()
            }
            is TransmissionResult.CommandNotConfigured -> {
                _uiState.update {
                    it.copy(
                        feedbackMessage = result.message
                    )
                }
                scheduleBannerClear()
            }
            is TransmissionResult.Failure -> {
                _uiState.update {
                    it.copy(
                        feedbackMessage = result.reason
                    )
                }
                scheduleBannerClear()
            }
        }
    }

    fun onPowerClicked() {
        val currentProfile = _uiState.value.activeProfile
        sendCommand("Power", currentProfile.powerCommand) {
            _uiState.update {
                it.copy(isPowerActive = !it.isPowerActive)
            }
        }
    }

    fun onSpeedClicked(speed: Int) {
        val currentProfile = _uiState.value.activeProfile
        val command = when (speed) {
            1 -> currentProfile.speed1Command
            2 -> currentProfile.speed2Command
            3 -> currentProfile.speed3Command
            4 -> currentProfile.speed4Command
            5 -> currentProfile.speed5Command
            else -> null
        }

        sendCommand("Speed $speed", command) {
            _uiState.update {
                it.copy(
                    selectedSpeed = speed,
                    isBoostActive = false,
                    isSleepActive = false
                )
            }
        }
    }

    fun onBoostClicked() {
        val currentProfile = _uiState.value.activeProfile
        sendCommand("Boost", currentProfile.boostCommand) {
            _uiState.update {
                it.copy(
                    isBoostActive = true,
                    selectedSpeed = null,
                    isSleepActive = false
                )
            }
        }
    }

    fun onSleepClicked() {
        val currentProfile = _uiState.value.activeProfile
        sendCommand("Sleep", currentProfile.sleepCommand) {
            _uiState.update {
                it.copy(
                    isSleepActive = !it.isSleepActive
                )
            }
        }
    }

    fun onLedClicked() {
        val currentProfile = _uiState.value.activeProfile
        sendCommand("LED", currentProfile.ledCommand) {
            _uiState.update {
                it.copy(
                    isLedActive = !it.isLedActive
                )
            }
        }
    }

    fun onTimerClicked() {
        val currentProfile = _uiState.value.activeProfile
        sendCommand("Timer", currentProfile.timerCommand) {}
    }

    fun dismissTimerDialog() {
        _isTimerDialogVisible.value = false
    }

    fun onTimerOptionSelected(hours: Int?) {
        _isTimerDialogVisible.value = false
        val currentProfile = _uiState.value.activeProfile
        sendCommand(if (hours != null) "Timer $hours hr" else "Timer", currentProfile.timerCommand) {}
    }

    fun openSettings() {
        triggerHapticFeedback()
        _isSettingsSheetVisible.value = true
    }

    fun dismissSettings() {
        _isSettingsSheetVisible.value = false
    }

    fun setHapticFeedback(enabled: Boolean) = preferencesRepository.setHapticFeedback(enabled)
    fun setButtonSound(enabled: Boolean) = preferencesRepository.setButtonSound(enabled)
    fun setIndicatorAnimation(enabled: Boolean) = preferencesRepository.setIndicatorAnimation(enabled)
    fun setCarrierFrequency(hz: Int) = preferencesRepository.setCarrierFrequency(hz)
    fun resetSettings() = preferencesRepository.resetDefaults()

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun triggerLedBlink() {
        if (!userPreferences.value.indicatorAnimationEnabled) return
        ledBlinkJob?.cancel()
        ledBlinkJob = viewModelScope.launch {
            _uiState.update { it.copy(isLedIndicatorLit = true) }
            delay(180)
            _uiState.update { it.copy(isLedIndicatorLit = false) }
        }
    }

    private fun scheduleBannerClear() {
        bannerClearJob?.cancel()
        bannerClearJob = viewModelScope.launch {
            delay(3500)
            _uiState.update { it.copy(feedbackMessage = null) }
        }
    }

    private fun triggerHapticFeedback() {
        if (!userPreferences.value.hapticFeedbackEnabled) return
        try {
            vibrator?.let { vib ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vib.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(25)
                }
            }
        } catch (_: Exception) {}
    }

    private fun triggerSoundFeedback() {
        if (!userPreferences.value.buttonSoundEnabled) return
        try {
            audioManager?.playSoundEffect(SoundEffectConstants.CLICK, 0.6f)
        } catch (_: Exception) {}
    }
}
