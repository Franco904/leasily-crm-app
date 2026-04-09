package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface WatchPropertyWithStaysTenantsUseCase {
    operator fun invoke(
        propertyId: Long,
    ): Flow<AppResult<Property?, ApplicationError>>
}
