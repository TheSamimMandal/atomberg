package com.example.model

import com.example.ir.AtombergIrCommands

/**
 * Represents a profile configuration for an Atomberg ceiling fan model.
 */
data class FanProfile(
    val id: String,
    val name: String,
    val description: String,
    val powerCommand: Long = AtombergIrCommands.POWER,
    val speed1Command: Long = AtombergIrCommands.SPEED_1,
    val speed2Command: Long = AtombergIrCommands.SPEED_2,
    val speed3Command: Long = AtombergIrCommands.SPEED_3,
    val speed4Command: Long = AtombergIrCommands.SPEED_4,
    val speed5Command: Long = AtombergIrCommands.SPEED_5,
    val boostCommand: Long = AtombergIrCommands.BOOST,
    val sleepCommand: Long = AtombergIrCommands.SLEEP,
    val ledCommand: Long? = AtombergIrCommands.LED,
    val timerCommand: Long? = AtombergIrCommands.TIMER
) {
    companion object {
        /**
         * Default profile using verified 32-bit NEC commands from Atomberg-BLDC-IR-Remote reference.
         */
        val ATOMBERG_UNIVERSAL = FanProfile(
            id = "atomberg_universal",
            name = "Atomberg Universal Remote",
            description = "Verified 32-bit NEC profile (38 kHz) for Atomberg BLDC ceiling fans",
            powerCommand = AtombergIrCommands.POWER,
            speed1Command = AtombergIrCommands.SPEED_1,
            speed2Command = AtombergIrCommands.SPEED_2,
            speed3Command = AtombergIrCommands.SPEED_3,
            speed4Command = AtombergIrCommands.SPEED_4,
            speed5Command = AtombergIrCommands.SPEED_5,
            boostCommand = AtombergIrCommands.BOOST,
            sleepCommand = AtombergIrCommands.SLEEP,
            ledCommand = null, // LED is unconfigured in reference
            timerCommand = AtombergIrCommands.TIMER // 0x6996F300
        )

        val DEFAULT_PROFILES = listOf(ATOMBERG_UNIVERSAL)
    }
}
