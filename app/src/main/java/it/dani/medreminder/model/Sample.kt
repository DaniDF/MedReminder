package it.dani.medreminder.model

import java.io.Serializable
import java.time.LocalDateTime

/**
 * @author Daniele
 *
 * This class represents a sample that contains a list of measures and when they are sampled
 *
 * @param[measures] The list of measures
 * @param[datetime] When the measure are sampled
 */
data class Sample(val measures : List<Measure> = ArrayList(), val datetime : LocalDateTime) : Serializable {
    override fun toString(): String {
        val result = StringBuilder("${this.datetime}\n")

        this.measures.forEach { measure ->
            result.append("$measure\n")
        }

        return result.toString()
    }
}