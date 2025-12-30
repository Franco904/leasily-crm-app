package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface TenantValidator {
    fun validate(
        tenant: Tenant,
        isThereStoredTenantWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError>
}
