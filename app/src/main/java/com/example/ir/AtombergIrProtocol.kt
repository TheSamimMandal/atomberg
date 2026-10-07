package com.example.ir

/**
 * NEC Infrared (IR) Protocol Encoder for Atomberg ceiling fans.
 *
 * Implements the verified NEC-style signal generator from the reference implementation:
 * - Carrier frequency: 38,000 Hz
 * - Header mark: 9000 µs, space: 4500 µs
 * - 32 data bits (LSB-first):
 *     - Bit mark: 560 µs
 *     - Logical '1': 1690 µs space
 *     - Logical '0': 560 µs space
 * - Final stop mark: 560 µs
 * - Pattern length: exactly 67 pulses (2 header + 64 data + 1 trailing mark)
 */
data class AtombergIrProtocol(
    val carrierFrequencyHz: Int = 38000,
    val bitCount: Int = 32,
    val headerMarkMicros: Int = 9000,
    val headerSpaceMicros: Int = 4500,
    val bitMarkMicros: Int = 560,
    val bitOneSpaceMicros: Int = 1690,
    val bitZeroSpaceMicros: Int = 560,
    val finalMarkMicros: Int = 560
) {

    /**
     * Builds the NEC signal pattern from a 32-bit command code.
     *
     * Reference implementation:
     * ```
     * int[] pattern = new int[67];
     * pattern[0] = 9000;
     * pattern[1] = 4500;
     * for (int i = 0; i < 32; i++) {
     *     pattern[2 + (i * 2)] = 560;
     *     if (((code >> i) & 1) == 1) {
     *         pattern[3 + (i * 2)] = 1690;
     *     } else {
     *         pattern[3 + (i * 2)] = 560;
     *     }
     * }
     * pattern[66] = 560;
     * return pattern;
     * ```
     */
    fun buildNecSignal(code: Long): IntArray {
        val pattern = IntArray(67)
        pattern[0] = headerMarkMicros
        pattern[1] = headerSpaceMicros

        for (i in 0 until 32) {
            pattern[2 + (i * 2)] = bitMarkMicros
            if (((code shr i) and 1L) == 1L) {
                pattern[3 + (i * 2)] = bitOneSpaceMicros
            } else {
                pattern[3 + (i * 2)] = bitZeroSpaceMicros
            }
        }

        pattern[66] = finalMarkMicros
        return pattern
    }

    /**
     * Alias for buildNecSignal.
     */
    fun encode(code: Long): IntArray = buildNecSignal(code)

    companion object {
        val DEFAULT = AtombergIrProtocol()
    }
}
