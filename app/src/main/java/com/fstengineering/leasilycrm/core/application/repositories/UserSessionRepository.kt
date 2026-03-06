package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface UserSessionRepository {
    suspend fun isSessionAlive(): AppResult<Boolean, ApplicationError>

    suspend fun killSession(): AppResult<Unit, ApplicationError>
}
