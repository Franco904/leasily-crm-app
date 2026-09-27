package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface OccupiedPeriodQueryRepository {
    suspend fun getStayById(id: Long): AppResult<StayRawReadModel, ApplicationError>

    suspend fun getUnavailablePeriodById(id: Long): AppResult<UnavailablePeriodRawReadModel, ApplicationError>

    suspend fun getOccupiedPeriodsByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<OccupiedPeriodReadModel>, ApplicationError>
}
