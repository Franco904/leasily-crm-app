package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.application.models.toDateTime

class AppDateTimeTypeConverter {
    @TypeConverter
    fun fromAppDateTime(dateTime: AppDateTime?): Long? = dateTime?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDateTime(millis: Long?): AppDateTime? = millis?.toDateTime()
}
