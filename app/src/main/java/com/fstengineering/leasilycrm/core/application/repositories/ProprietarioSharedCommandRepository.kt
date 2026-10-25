package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface ProprietarioSharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>
}
