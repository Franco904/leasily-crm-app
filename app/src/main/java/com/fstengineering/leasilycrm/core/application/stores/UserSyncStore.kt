package com.fstengineering.leasilycrm.core.application.stores

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface UserSyncStore {
    suspend fun retryUpdate(): AppResult<Unit, ApplicationError>
}
