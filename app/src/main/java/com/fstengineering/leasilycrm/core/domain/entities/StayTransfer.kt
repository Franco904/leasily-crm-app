package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayTransfer private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val date: AppDate,
    val amount: AppDecimal,
    val description: String?,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            date: AppDate?,
            amount: AppDecimal?,
            description: String?,
        ): StayTransfer {
            validateDate(date).onError { throw it.asException() }
            validateAmount(amount).onError { throw it.asException() }

            return StayTransfer(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                date = requireNotNull(date),
                amount = requireNotNull(amount),
                description = description,
            )
        }

        fun validateDate(date: AppDate?): AppResult<Unit, DomainError.Stay> {
            return when {
                date == null -> AppResult.Error(DomainError.Stay.TransferDateRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateAmount(amount: AppDecimal?): AppResult<Unit, DomainError.Stay> {
            return when {
                amount == null -> AppResult.Error(DomainError.Stay.TransferAmountRequired)
                amount.isNegative -> AppResult.Error(DomainError.Stay.TransferAmountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
