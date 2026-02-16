package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validatePropertyPriceAmount
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyPricePeriod

class PropertyPrice private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val period: PropertyPricePeriod,
    val amount: AppDecimal,
    val propertyId: Long,
    val isDeleted: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            startDate: AppDate?,
            endDate: AppDate?,
            amount: AppDecimal?,
            propertyId: Long,
            isDeleted: Boolean,
        ): PropertyPrice {
            validatePropertyPriceAmount(amount)
                .onError { throw it.asException() }

            val period = PropertyPricePeriod.parse(
                startDate = startDate,
                endDate = endDate,
            )

            return PropertyPrice(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                period = period,
                amount = requireNotNull(amount),
                propertyId = propertyId,
                isDeleted = isDeleted,
            )
        }
    }
}
