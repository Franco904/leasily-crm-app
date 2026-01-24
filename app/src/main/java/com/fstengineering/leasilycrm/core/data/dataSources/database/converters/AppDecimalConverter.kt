package com.fstengineering.leasilycrm.core.data.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import com.fstengineering.leasilycrm.core.application.models.toDecimalOrNull

class AppDecimalConverter {
    @TypeConverter
    fun fromAppDecimal(value: AppDecimal?): String? = value?.toString()

    @TypeConverter
    fun toAppDecimal(value: String?): AppDecimal? = value?.toDecimalOrNull()
}
