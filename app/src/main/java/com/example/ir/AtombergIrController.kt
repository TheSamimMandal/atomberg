package com.example.ir

import android.hardware.ConsumerIrManager
import android.util.Log

sealed class TransmissionResult {
    data class Success(
        val commandName: String,
        val hexString: String,
        val carrierFrequencyHz: Int,
        val patternLength: Int
    ) : TransmissionResult()

    data class HardwareUnavailable(
        val reason: String
    ) : TransmissionResult()

    data class CommandNotConfigured(
        val message: String
    ) : TransmissionResult()

    data class Failure(
        val reason: String
    ) : TransmissionResult()
}

class AtombergIrController(
    private val hardwareDetector: IrHardwareDetector,
    var protocol: AtombergIrProtocol = AtombergIrProtocol.DEFAULT
) {

    companion object {
        private const val TAG = "AtombergIrController"
    }

    /**
     * Transmits a named command using the verified NEC IR protocol at 38 kHz.
     */
    fun transmitCommand(commandName: String, commandHex: Long?): TransmissionResult {
        if (commandHex == null) {
            val message = if (commandName.equals("LED", ignoreCase = true)) {
                "LED IR command is not configured for this remote."
            } else {
                "IR command is not configured."
            }
            Log.w(TAG, message)
            return TransmissionResult.CommandNotConfigured(message)
        }

        val hardwareStatus = hardwareDetector.checkHardwareStatus()
        if (hardwareStatus is IrHardwareStatus.NotAvailable) {
            Log.w(TAG, "IR transmission aborted: ${hardwareStatus.reason}")
            return TransmissionResult.HardwareUnavailable(hardwareStatus.reason)
        }

        val irManager: ConsumerIrManager? = hardwareDetector.consumerIrManager
        if (irManager == null || !irManager.hasIrEmitter()) {
            return TransmissionResult.HardwareUnavailable("No IR blaster detected on this phone.")
        }

        return try {
            val pattern = protocol.buildNecSignal(commandHex)
            val carrierHz = protocol.carrierFrequencyHz

            // Transmit through Android ConsumerIrManager
            irManager.transmit(carrierHz, pattern)

            val hexStr = AtombergIrCommands.toHexString(commandHex)
            Log.i(TAG, "Transmitted $commandName ($hexStr) at ${carrierHz}Hz [${pattern.size} pulses]")

            TransmissionResult.Success(
                commandName = commandName,
                hexString = hexStr,
                carrierFrequencyHz = carrierHz,
                patternLength = pattern.size
            )
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing TRANSMIT_IR permission", e)
            TransmissionResult.Failure("Missing TRANSMIT_IR permission.")
        } catch (e: Exception) {
            Log.e(TAG, "IR transmission failed", e)
            TransmissionResult.Failure("IR transmission failed.")
        }
    }
}
