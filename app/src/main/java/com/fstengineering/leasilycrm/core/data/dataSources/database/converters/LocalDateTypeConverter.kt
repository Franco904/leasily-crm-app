package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateTypeConverter {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun toLocalDate(dateText: String?): LocalDate? {
        return dateText?.let { LocalDate.parse(it) }
    }
}
