package com.fstengineering.leasilycrm.propertyStays.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyStays.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayQueryRepository {
    fun watchAllByPropertyId(
        propertyId: Long,
    ): Flow<AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError>>
}
