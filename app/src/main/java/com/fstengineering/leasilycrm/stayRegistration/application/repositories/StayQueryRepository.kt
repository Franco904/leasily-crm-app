package com.fstengineering.leasilycrm.stayRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface StayQueryRepository {
    suspend fun getStayById(id: Long): AppResult<StayRawReadModel, ApplicationError>

    suspend fun getUnavailablePeriodById(id: Long): AppResult<UnavailablePeriodRawReadModel, ApplicationError>

    suspend fun getAllByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<Pair<AppDateTime, AppDateTime>>, ApplicationError>
}
