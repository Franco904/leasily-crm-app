package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeTypeConverter {
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): Long? {
        return dateTime
            ?.atZone(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
    }

    @TypeConverter
    fun toLocalDateTime(millis: Long?): LocalDateTime? {
        return millis?.let {
            Instant
                .ofEpochMilli(/* epochMilli = */ it)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime()
        }
    }
}
