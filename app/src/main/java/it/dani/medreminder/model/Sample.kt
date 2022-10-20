package it.dani.medreminder.model

import java.io.Serializable
import java.time.LocalDateTime

data class Sample(val measures : List<Measure> = ArrayList(), val datetime : LocalDateTime) : Serializable