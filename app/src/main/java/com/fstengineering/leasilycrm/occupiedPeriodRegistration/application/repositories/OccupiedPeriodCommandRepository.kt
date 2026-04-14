package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.UnavailablePeriod

interface OccupiedPeriodCommandRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, ApplicationError>

    suspend fun insertOrUpdate(unavailablePeriod: UnavailablePeriod): AppResult<Unit, ApplicationError>
}
