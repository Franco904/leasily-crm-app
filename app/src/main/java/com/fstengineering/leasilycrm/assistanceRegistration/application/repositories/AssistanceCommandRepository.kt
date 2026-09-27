package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface AssistanceCommandRepository {
    suspend fun insertOrUpdate(assistance: Assistance): AppResult<Unit, ApplicationError>

    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>
}