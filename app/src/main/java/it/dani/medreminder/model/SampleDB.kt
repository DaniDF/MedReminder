package it.dani.medreminder.model

import java.io.Serializable

/**
 * @author Daniele
 *
 * This class contains all the samples ever measured
 *
 * @param[samples] The list of samples
 */
data class SampleDB(var samples : List<Sample> = ArrayList()) : Serializable {
    override fun toString(): String {
        val result = StringBuilder()

        this.samples.forEach {
            result.append(samples.toString().replace("\n","\n\t"))
        }

        return result.toString()
    }
}