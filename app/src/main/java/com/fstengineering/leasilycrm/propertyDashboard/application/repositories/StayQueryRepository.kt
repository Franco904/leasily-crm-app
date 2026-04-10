package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface StayQueryRepository {
    suspend fun getAllByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError>
}
