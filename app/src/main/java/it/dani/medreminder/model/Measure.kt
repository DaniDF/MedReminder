package it.dani.medreminder.model

import java.io.Serializable

data class Measure(val measureLabel : MeasureTypes, val measureValue : Float) : Serializable