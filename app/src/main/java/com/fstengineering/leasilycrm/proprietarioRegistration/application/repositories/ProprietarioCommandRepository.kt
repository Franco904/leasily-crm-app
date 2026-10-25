package com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario

interface ProprietarioCommandRepository {
    suspend fun insertOrUpdate(proprietario: Proprietario): AppResult<Unit, ApplicationError>
}
