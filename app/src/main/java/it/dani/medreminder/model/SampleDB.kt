package it.dani.medreminder.model

import com.google.gson.annotations.Expose
import java.io.Serializable

/**
 * @author Daniele
 *
 * This class contains all the samples ever measured
 *
 * @param[samples] The list of samples
 */
data class SampleDB(@Expose var samples : MutableList<Sample> = ArrayList()) : Serializable {
    override fun toString(): String {
        val result = StringBuilder()

        this.samples.forEach { sample ->
            result.append(sample.toString().replace("\n","\n\t"))
        }

        return result.toString()
    }
}