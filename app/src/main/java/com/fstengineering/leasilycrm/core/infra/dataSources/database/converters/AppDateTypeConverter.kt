package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDate

class AppDateTypeConverter {
    @TypeConverter
    fun fromAppDate(date: AppDate?): Long? = date?.toEpochMillisUTC()

    @TypeConverter
    fun toAppDate(millis: Long?): AppDate? = millis?.toDate()
}
