package it.dani.medreminder.model

import java.io.Serializable

/**
 * @author Daniele
 *
 * This class implements the measure object
 *
 * @param[measureLabel] [MeasureTypes] The type of the measure
 * @param[measureValue] The value of the measure
 */
data class Measure(var measureLabel : MeasureTypes, var measureValue : Float) : Serializable {
    override fun toString(): String {
        return "${this.measureLabel}: ${this.measureValue}"
    }
}