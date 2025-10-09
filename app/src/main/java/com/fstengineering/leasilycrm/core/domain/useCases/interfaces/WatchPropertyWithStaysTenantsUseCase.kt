package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.coroutines.flow.Flow

interface WatchPropertyWithStaysTenantsUseCase {
    operator fun invoke(
        propertyId: Long,
    ): Flow<AppResult<Property?, DataSourceError>>
}
