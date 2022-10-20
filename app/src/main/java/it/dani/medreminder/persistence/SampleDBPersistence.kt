package it.dani.medreminder.persistence

import it.dani.medreminder.model.SampleDB

/**
 * @author Daniele
 */

interface SampleDBPersistence {
    /**
     * This function loads a [SampleDB]
     * @return The loaded [SampleDB]
     */
    fun loadDB() : SampleDB

    /**
     * This function stores a [SampleDB]
     */
    fun storeDB(sampleDB : SampleDB)
}