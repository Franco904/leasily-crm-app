package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.toDateTime

class AppDateTimeTypeConverter {
    @TypeConverter
    fun fromAppDateTime(dateTime: AppDateTime?): Long? = dateTime?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDateTime(millis: Long?): AppDateTime? = millis?.toDateTime()
}
