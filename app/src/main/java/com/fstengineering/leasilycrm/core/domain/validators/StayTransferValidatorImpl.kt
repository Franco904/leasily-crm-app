package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayTransferValidator

class StayTransferValidatorImpl : StayTransferValidator {
    override fun validate(
        stayTransfer: StayTransfer,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateTransferDate(transferDate = stayTransfer.transferDate)
            .onError { e -> errors.add(e) }

        validateTransferAmount(amount = stayTransfer.amount)
            .onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateTransferDate(
        transferDate: AppDate?,
    ): AppResult<Unit, DomainError.StayTransferDate> {
        return when {
            transferDate == null -> {
                AppResult.Error(DomainError.StayTransferDate.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateTransferAmount(
        amount: AppDecimal?,
    ): AppResult<Unit, DomainError.StayTransferAmount> {
        return when {
            amount == null -> {
                AppResult.Error(DomainError.StayTransferAmount.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
