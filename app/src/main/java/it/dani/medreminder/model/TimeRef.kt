package it.dani.medreminder.model

import com.google.gson.annotations.Expose
import java.io.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

/**
 * @author Daniele
 *
 * This class is a wrapper for [ZonedDateTime] because it has problems with Gson serialization
 */
class TimeRef(@Expose val year : Int,
              @Expose val month : Int,
              @Expose val day : Int,
              @Expose val hour : Int,
              @Expose val min : Int,
              @Expose val sec : Int,
              @Expose val nano : Int,
              @Expose val zoneId: String)
    : Serializable,Comparable<TimeRef> {

    constructor(zonedDateTime: ZonedDateTime) : this(zonedDateTime.year,
        zonedDateTime.monthValue,
        zonedDateTime.dayOfMonth,
        zonedDateTime.hour,
        zonedDateTime.minute,
        zonedDateTime.second,
        zonedDateTime.nano,
        zonedDateTime.zone.id)

    /**
     * This method transform this class in a [ZonedDateTime] object
     *
     * @return The computed object
     */
    private fun getZonedDateTime() : ZonedDateTime {
        return ZonedDateTime.of(this.year,
        this.month,
        this.day,
        this.hour,
        this.min,
        this.sec,
        this.nano,
        ZoneId.of(this.zoneId))
    }

    override fun compareTo(other: TimeRef): Int {
        return this.getZonedDateTime().compareTo(other.getZonedDateTime())
    }

    /**
     * This method generates the short version of this object
     *
     * @return The short string
     */
    fun toStringShort() : String {
        val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
        return this.getZonedDateTime().format(dateTimeFormatter)
    }

    /**
     * This method generates the full version of this object
     *
     * @return The full string
     */
    fun toStringLong() : String {
        val dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(Locale.getDefault())
        return this.getZonedDateTime().format(dateTimeFormatter)
    }

    override fun toString(): String {
        return this.toStringShort()
    }
}