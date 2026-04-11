package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimalOrNull

class AppDecimalConverter {
    @TypeConverter
    fun fromAppDecimal(value: AppDecimal?): String? = value?.toString()

    @TypeConverter
    fun toAppDecimal(value: String?): AppDecimal? = value?.toDecimalOrNull()
}
