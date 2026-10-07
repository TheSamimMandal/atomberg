package com.example

import com.example.ir.AtombergIrCommands
import com.example.ir.AtombergIrProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun verifyVerifiedAtomberg32BitHexCommands() {
        assertEquals("0x6E91F300", AtombergIrCommands.toHexString(AtombergIrCommands.POWER))
        assertEquals("0x748BF300", AtombergIrCommands.toHexString(AtombergIrCommands.SPEED_1))
        assertEquals("0x6F90F300", AtombergIrCommands.toHexString(AtombergIrCommands.SPEED_2))
        assertEquals("0x758AF300", AtombergIrCommands.toHexString(AtombergIrCommands.SPEED_3))
        assertEquals("0x6C93F300", AtombergIrCommands.toHexString(AtombergIrCommands.SPEED_4))
        assertEquals("0x7788F300", AtombergIrCommands.toHexString(AtombergIrCommands.SPEED_5))
        assertEquals("0x708FF300", AtombergIrCommands.toHexString(AtombergIrCommands.BOOST))
        assertEquals("0x6996F300", AtombergIrCommands.toHexString(AtombergIrCommands.TIMER))
        assertEquals("0x718EF300", AtombergIrCommands.toHexString(AtombergIrCommands.SLEEP))
        assertNull("LED IR command must remain unconfigured", AtombergIrCommands.LED)
    }

    @Test
    fun verifyNecSignalPatternGeneration() {
        val protocol = AtombergIrProtocol.DEFAULT
        assertEquals(38000, protocol.carrierFrequencyHz)
        assertEquals(32, protocol.bitCount)

        val pattern = protocol.buildNecSignal(AtombergIrCommands.POWER)
        // 2 (header) + 32 * 2 (bits) + 1 (final mark) = 67 integers
        assertEquals(67, pattern.size)

        // Header pulses
        assertEquals(9000, pattern[0])
        assertEquals(4500, pattern[1])

        // First bit (bit 0 of 0x6E91F300: lowest bit of 0x00 is 0)
        // Mark = 560
        assertEquals(560, pattern[2])
        // Space for 0 = 560
        assertEquals(560, pattern[3])

        // Final mark
        assertEquals(560, pattern[66])
    }
}
