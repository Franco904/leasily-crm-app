package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface OccupiedPeriodQueryRepository {
    suspend fun getStayById(id: Long): AppResult<StayRawReadModel>

    suspend fun getUnavailablePeriodById(id: Long): AppResult<UnavailablePeriodRawReadModel>

    suspend fun getOccupiedPeriodsByImovelId(
        imovelId: Long,
    ): AppResult<ImmutableList<OccupiedPeriodReadModel>>
}
