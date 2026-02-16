package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateStayTransferAmount
import com.fstengineering.leasilycrm.core.domain.validations.validateStayTransferDate

class StayTransfer private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val transferDate: AppDate,
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
            transferDate: AppDate?,
            amount: AppDecimal?,
            description: String?,
            stayId: Long,
            isDeleted: Boolean,
        ): StayTransfer {
            validateStayTransferDate(transferDate).onError { throw it.asException() }
            validateStayTransferAmount(amount).onError { throw it.asException() }

            return StayTransfer(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                transferDate = requireNotNull(transferDate),
                amount = requireNotNull(amount),
                description = description,
                stayId = stayId,
                isDeleted = isDeleted,
            )
        }
    }
}
