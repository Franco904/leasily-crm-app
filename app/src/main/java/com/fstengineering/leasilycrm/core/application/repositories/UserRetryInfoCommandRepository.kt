package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface UserRetryInfoCommandRepository {
    suspend fun retryUpdate(): AppResult<Unit, ApplicationError>
}
