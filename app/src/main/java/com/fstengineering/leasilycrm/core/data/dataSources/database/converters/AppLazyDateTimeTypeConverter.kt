package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.application.models.AppDateTime

class AppLazyDateTimeTypeConverter {
    @TypeConverter
    fun fromAppLazyDateTime(dateTime: AppDateTime?): Long? {
        return dateTime?.toEpochMillisUTC()
    }

    @TypeConverter
    fun toAppLazyDateTime(millis: Long?): AppDateTime? {
        return millis?.let { AppDateTime.fromMillis(millis) }
    }
}
