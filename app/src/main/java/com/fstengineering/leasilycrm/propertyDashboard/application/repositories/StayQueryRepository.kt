package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayQueryRepository {
    fun watchAllByPropertyId(
        propertyId: Long,
    ): Flow<AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError>>
}
