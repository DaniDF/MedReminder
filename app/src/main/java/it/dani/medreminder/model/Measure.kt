package it.dani.medreminder.model

import java.io.Serializable

data class Measure(var measureLabel : MeasureTypes, var measureValue : Float) : Serializable