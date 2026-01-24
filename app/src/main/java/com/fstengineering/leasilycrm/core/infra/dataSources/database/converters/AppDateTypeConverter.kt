package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.toDate

class AppDateTypeConverter {
    @TypeConverter
    fun fromAppDate(date: AppDate?): Long? = date?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDate(millis: Long?): AppDate? = millis?.toDate()
}
