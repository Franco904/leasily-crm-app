package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDateTime

class AppDateTimeTypeConverter {
    @TypeConverter
    fun fromAppDateTime(dateTime: AppDateTime?): Long? = dateTime?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDateTime(millis: Long?): AppDateTime? = millis?.toDateTime()
}
