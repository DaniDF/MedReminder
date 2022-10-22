package it.dani.medreminder.model

import it.dani.medreminder.R
import java.io.Serializable

/**
 * @author Daniele
 *
 * This class represents all the possible types af measures
 *
 * @param[humanReadableString] A human readable version of the represented type, the reference of the string resource
 */
enum class MeasureTypes(val humanReadableString : Int) : Serializable {
    HEART_RATE(R.string.measure_type_heart_rate),
    BLOOD_OXYGENATION(R.string.measure_type_blood_oxygenation),
    BLOOD_PRESSURE(R.string.measure_type_blood_pressure),
    GLYCEMIA(R.string.measure_type_glycemia)
}