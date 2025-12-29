package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayTransferValidator
import java.math.BigDecimal
import java.time.LocalDate

class StayTransferValidatorImpl : StayTransferValidator {
    override fun validate(
        stayTransfer: StayTransfer,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateTransferDate(transferDate = stayTransfer.transferDate)
            .onError { e -> errors.add(e) }

        validateTransferAmount(amount = stayTransfer.transferAmount)
            .onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateTransferDate(
        transferDate: LocalDate?,
    ): AppResult<Unit, DomainError.StayTransferDate> {
        return when {
            transferDate == null -> {
                AppResult.Error(DomainError.StayTransferDate.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateTransferAmount(
        amount: BigDecimal?,
    ): AppResult<Unit, DomainError.StayTransferAmount> {
        return when {
            amount == null -> {
                AppResult.Error(DomainError.StayTransferAmount.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
