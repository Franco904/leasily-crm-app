package com.fstengineering.leasilycrm.propertyRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Amount
import com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects.PropertyPricePeriod
import kotlinx.collections.immutable.toPersistentList

class PropertyPrice private constructor(
    val id: Long?,
    val period: PropertyPricePeriod,
    val amount: Amount,
) {
    companion object {
        fun parse(
            id: Long? = null,
            startDate: AppDate? = null,
            endDate: AppDate? = null,
            amount: AppDecimal? = null,
        ): AppResultCompound<PropertyPrice, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (startDate == null || endDate == null) errors.add(DomainError.PropertyPricePeriod.Required)
            if (amount == null) errors.add(DomainError.Amount.Required)

            val period =
                if (startDate == null || endDate == null) null
                else PropertyPricePeriod.parse(
                    startDate = startDate,
                    endDate = endDate,
                )
                    .fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val amount =
                if (amount == null) null
                else Amount.parse(amount)
                    .fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = PropertyPrice(
                id = id,
                period = requireNotNull(period),
                amount = requireNotNull(amount),
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
