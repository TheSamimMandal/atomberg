package com.example.model

/**
 * State representing the remote controller UI.
 *
 * NOTE: Standard infrared transmission is inherently one-way without return telemetry.
 * All states reflect "Last command sent" from the remote rather than verified physical fan telemetry.
 */
data class RemoteState(
    val lastCommandName: String? = null,
    val lastCommandHex: String? = null,
    val lastSentTimestamp: Long = 0L,
    val selectedSpeed: Int? = null, // 1..5
    val isBoostActive: Boolean = false,
    val isSleepActive: Boolean = false,
    val isLedActive: Boolean = false,
    val isPowerActive: Boolean = false,
    val isLedIndicatorLit: Boolean = false,
    val isHardwareAvailable: Boolean = false,
    val hardwareStatusText: String = "Checking IR...",
    val hardwareDetails: String = "",
    val feedbackMessage: String? = null,
    val activeProfile: FanProfile = FanProfile.ATOMBERG_UNIVERSAL
)
