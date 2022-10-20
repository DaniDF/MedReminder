package it.dani.medreminder.model

import java.io.Serializable

data class SampleDB(var samples : List<Sample> = ArrayList()) : Serializable