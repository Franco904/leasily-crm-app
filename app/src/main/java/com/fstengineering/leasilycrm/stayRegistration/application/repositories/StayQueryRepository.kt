package com.fstengineering.leasilycrm.stayRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.OccupiedPeriod
import kotlinx.collections.immutable.ImmutableList

interface StayQueryRepository {
    suspend fun getStayById(id: Long): AppResult<StayRawReadModel, ApplicationError>

    suspend fun getUnavailablePeriodById(id: Long): AppResult<UnavailablePeriodRawReadModel, ApplicationError>

    suspend fun getOccupiedPeriodsByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<OccupiedPeriod>, ApplicationError>
}
