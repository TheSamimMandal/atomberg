package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferences(
    val hapticFeedbackEnabled: Boolean = true,
    val buttonSoundEnabled: Boolean = false,
    val indicatorAnimationEnabled: Boolean = true,
    val carrierFrequencyHz: Int = 38000
)

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("atomberg_remote_prefs", Context.MODE_PRIVATE)

    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val haptics = prefs.getBoolean(KEY_HAPTICS, true)
        val sound = prefs.getBoolean(KEY_SOUND, false)
        val indicator = prefs.getBoolean(KEY_INDICATOR, true)
        val carrier = prefs.getInt(KEY_CARRIER, 38000)

        return UserPreferences(
            hapticFeedbackEnabled = haptics,
            buttonSoundEnabled = sound,
            indicatorAnimationEnabled = indicator,
            carrierFrequencyHz = carrier
        )
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(hapticFeedbackEnabled = enabled)
    }

    fun setButtonSound(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(buttonSoundEnabled = enabled)
    }

    fun setIndicatorAnimation(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_INDICATOR, enabled).apply()
        _userPreferences.value = _userPreferences.value.copy(indicatorAnimationEnabled = enabled)
    }

    fun setCarrierFrequency(hz: Int) {
        prefs.edit().putInt(KEY_CARRIER, hz).apply()
        _userPreferences.value = _userPreferences.value.copy(carrierFrequencyHz = hz)
    }

    fun resetDefaults() {
        prefs.edit().clear().apply()
        _userPreferences.value = UserPreferences()
    }

    companion object {
        private const val KEY_HAPTICS = "key_haptics"
        private const val KEY_SOUND = "key_sound"
        private const val KEY_INDICATOR = "key_indicator"
        private const val KEY_CARRIER = "key_carrier"
    }
}
