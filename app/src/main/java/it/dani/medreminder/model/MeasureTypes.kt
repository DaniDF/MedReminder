package it.dani.medreminder.model

import it.dani.medreminder.R

enum class MeasureTypes(val humanReadableString : Int) {
    HEART_RATE(R.string.measure_type_heart_rate),
    BLOOD_OXYGENATION(R.string.measure_type_blood_oxygenation),
    BLOOD_PRESSURE(R.string.measure_type_blood_pressure),
    GLYCEMIA(R.string.measure_type_glycemia)
}