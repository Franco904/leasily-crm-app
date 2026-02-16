package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validatePropertyPricePeriod

class PropertyPricePeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    companion object {
        fun parse(
            startDate: AppDate?,
            endDate: AppDate?,
        ): PropertyPricePeriod {
            validatePropertyPricePeriod(startDate, endDate)
                .onError { throw it.asException() }

            return PropertyPricePeriod(
                startDate = requireNotNull(startDate),
                endDate = requireNotNull(endDate),
            )
        }
    }
}
