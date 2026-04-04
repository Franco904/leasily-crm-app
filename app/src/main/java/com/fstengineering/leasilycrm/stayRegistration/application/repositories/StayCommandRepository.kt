package com.fstengineering.leasilycrm.stayRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.UnavailablePeriod

interface StayCommandRepository {
    suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, ApplicationError>

    suspend fun insertOrUpdate(unavailablePeriod: UnavailablePeriod): AppResult<Unit, ApplicationError>
}
