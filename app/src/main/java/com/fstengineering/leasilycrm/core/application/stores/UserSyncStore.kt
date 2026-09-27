package com.fstengineering.leasilycrm.core.application.stores

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface UserSyncStore {
    suspend fun retryUpdate(): AppResult<Unit, ApplicationError>
}
