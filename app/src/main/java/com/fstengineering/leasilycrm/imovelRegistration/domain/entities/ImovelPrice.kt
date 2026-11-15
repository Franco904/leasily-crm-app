package com.fstengineering.leasilycrm.imovelRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Amount
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.imovelRegistration.domain.valueObjects.ImovelPricePeriod
import kotlinx.collections.immutable.toPersistentList

class ImovelPrice private constructor(
    val id: Long?,
    val period: ImovelPricePeriod,
    val amount: Amount,
) {
    companion object {
        fun parse(
            id: Long? = null,
            startDate: AppDate? = null,
            endDate: AppDate? = null,
            amount: AppDecimal? = null,
            otherPeriods: List<ImovelPricePeriod> = emptyList(),
        ): AppResult<ImovelPrice, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (startDate == null || endDate == null) errors.add(DomainError.ImovelPricePeriod.Required)
            if (amount == null) errors.add(DomainError.Amount.Required)

            val period =
                if (startDate == null || endDate == null) null
                else ImovelPricePeriod.parse(
                    startDate = startDate,
                    endDate = endDate,
                    otherPeriods = otherPeriods,
                )
                    .fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = ImovelPrice(
                id = id,
                period = requireNotNull(period),
                amount = Amount.of(requireNotNull(amount)),
            )

            return AppResult.Success(data = parsed)
        }
    }
}
