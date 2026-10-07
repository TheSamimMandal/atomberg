package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager

sealed class IrHardwareStatus {
    data class Available(
        val carrierRanges: List<CarrierRange>,
        val supports38kHz: Boolean
    ) : IrHardwareStatus()

    data class NotAvailable(
        val reason: String
    ) : IrHardwareStatus()
}

data class CarrierRange(
    val minFrequencyHz: Int,
    val maxFrequencyHz: Int
) {
    override fun toString(): String = "${minFrequencyHz / 1000}kHz - ${maxFrequencyHz / 1000}kHz"
}

class IrHardwareDetector(private val context: Context) {

    val consumerIrManager: ConsumerIrManager? by lazy {
        try {
            context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Inspects the device for a physical Consumer IR blaster emitter.
     */
    fun checkHardwareStatus(): IrHardwareStatus {
        val manager = consumerIrManager
            ?: return IrHardwareStatus.NotAvailable("No IR blaster detected on this phone.")

        return try {
            if (!manager.hasIrEmitter()) {
                IrHardwareStatus.NotAvailable("No IR blaster detected on this phone.")
            } else {
                val ranges = try {
                    manager.carrierFrequencies?.map {
                        CarrierRange(it.minFrequency, it.maxFrequency)
                    } ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }

                val supports38k = if (ranges.isEmpty()) {
                    true // Most chipsets support standard 38kHz by default
                } else {
                    ranges.any { it.minFrequencyHz <= 38000 && it.maxFrequencyHz >= 38000 }
                }

                IrHardwareStatus.Available(
                    carrierRanges = ranges,
                    supports38kHz = supports38k
                )
            }
        } catch (e: Exception) {
            IrHardwareStatus.NotAvailable("No IR blaster detected on this phone.")
        }
    }
}
