package it.dani.medreminder.model

import com.google.gson.annotations.Expose
import java.io.Serializable

/**
 * @author Daniele
 *
 * This class represents a sample that contains a list of measures and when they are sampled
 *
 * @param[measures] The list of measures
 * @param[datetime] When the measure are sampled
 */
data class Sample(@Expose val measures : List<Measure> = ArrayList(), @Expose val datetime : TimeRef) : Serializable {

    override fun toString(): String {
        val result = StringBuilder("${this.datetime}\n")

        this.measures.forEach { measure ->
            result.append("$measure\n")
        }

        return result.toString()
    }
}