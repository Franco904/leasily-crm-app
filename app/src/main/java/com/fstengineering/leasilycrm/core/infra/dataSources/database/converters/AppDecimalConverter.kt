package com.fstengineering.leasilycrm.core.infra.dataSources.database.converters

import androidx.room.TypeConverter
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.helpers.toDecimalOrNull

class AppDecimalConverter {
    @TypeConverter
    fun fromAppDecimal(value: AppDecimal?): String? = value?.toString()

    @TypeConverter
    fun toAppDecimal(value: String?): AppDecimal? = value?.toDecimalOrNull()
}
