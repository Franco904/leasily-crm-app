package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.UnavailablePeriod

interface OccupiedPeriodCommandRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Unit>

    suspend fun insertOrUpdate(unavailablePeriod: UnavailablePeriod): AppResult<Unit>
}
