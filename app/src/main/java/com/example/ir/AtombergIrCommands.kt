package com.example.ir

/**
 * Verified 32-bit Hex IR command definitions for compatible Atomberg ceiling fans.
 * Reference implementation: Atomberg-BLDC-IR-Remote (NEC protocol, 38 kHz).
 *
 * Commands:
 * - POWER:  0x6E91F300
 * - SPEED 1: 0x748BF300
 * - SPEED 2: 0x6F90F300
 * - SPEED 3: 0x758AF300
 * - SPEED 4: 0x6C93F300
 * - SPEED 5: 0x7788F300
 * - BOOST:   0x708FF300
 * - TIMER:   0x6996F300
 * - SLEEP:   0x718EF300
 * - LED:     UNCONFIGURED (no verified code in reference implementation)
 */
object AtombergIrCommands {

    /**
     * Power ON/OFF command.
     * Hex: 0x6E91F300
     */
    const val POWER: Long = 0x6E91F300L

    /**
     * Fan Speed 1 command.
     * Hex: 0x748BF300
     */
    const val SPEED_1: Long = 0x748BF300L

    /**
     * Fan Speed 2 command.
     * Hex: 0x6F90F300
     */
    const val SPEED_2: Long = 0x6F90F300L

    /**
     * Fan Speed 3 command.
     * Hex: 0x758AF300
     */
    const val SPEED_3: Long = 0x758AF300L

    /**
     * Fan Speed 4 command.
     * Hex: 0x6C93F300
     */
    const val SPEED_4: Long = 0x6C93F300L

    /**
     * Fan Speed 5 command.
     * Hex: 0x7788F300
     */
    const val SPEED_5: Long = 0x7788F300L

    /**
     * Boost Mode command.
     * Hex: 0x708FF300
     */
    const val BOOST: Long = 0x708FF300L

    /**
     * Timer command.
     * Hex: 0x6996F300
     */
    const val TIMER: Long = 0x6996F300L

    /**
     * Sleep Mode command.
     * Hex: 0x718EF300
     */
    const val SLEEP: Long = 0x718EF300L

    /**
     * LED Light command is UNCONFIGURED in the reference implementation.
     * DO NOT invent or substitute fake codes.
     */
    val LED: Long? = null

    /**
     * Formats a Long hex command into a canonical hex string (e.g., "0x6E91F300").
     */
    fun toHexString(command: Long): String {
        return "0x" + command.toString(16).uppercase().padStart(8, '0')
    }
}
