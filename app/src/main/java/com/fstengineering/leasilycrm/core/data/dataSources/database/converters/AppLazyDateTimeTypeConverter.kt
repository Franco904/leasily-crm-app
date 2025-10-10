package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime

class AppLazyDateTimeTypeConverter {
    @TypeConverter
    fun fromAppLazyDateTime(dateTime: AppLazyDateTime?): Long? {
        return dateTime?.toEpochMillisUTC()
    }

    @TypeConverter
    fun toAppLazyDateTime(millis: Long?): AppLazyDateTime? {
        return millis?.let { AppLazyDateTime.fromMillis(millis) }
    }
}
