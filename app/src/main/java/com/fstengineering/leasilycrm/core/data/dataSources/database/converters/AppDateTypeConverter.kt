package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.toDate

class AppDateTypeConverter {
    @TypeConverter
    fun fromAppDate(date: AppDate?): Long? = date?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDate(millis: Long?): AppDate? = millis?.toDate()
}
