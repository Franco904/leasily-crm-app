package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface StayTransferValidator {
    fun validate(
        stayTransfer: StayTransfer,
    ): AppResultCompound<Unit, DomainError>
}
