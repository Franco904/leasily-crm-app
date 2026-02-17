package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class StayTransfer private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val date: AppDate,
    val amount: AppDecimal,
    val description: String?,
    val stayId: Long,
    val isDeleted: Boolean,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            date: AppDate?,
            amount: AppDecimal?,
            description: String?,
            stayId: Long,
            isDeleted: Boolean,
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
                stayId = stayId,
                isDeleted = isDeleted,
            )
        }

        fun validateDate(date: AppDate?): AppResult<Unit, ParsingError.Stay> {
            return when {
                date == null -> AppResult.Error(ParsingError.Stay.TransferDateRequired)
                else -> AppResult.Success(Unit)
            }
        }

        fun validateAmount(amount: AppDecimal?): AppResult<Unit, ParsingError.Stay> {
            return when {
                amount == null -> AppResult.Error(ParsingError.Stay.TransferAmountRequired)
                amount.isNegative -> AppResult.Error(ParsingError.Stay.TransferAmountIsNegative)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
