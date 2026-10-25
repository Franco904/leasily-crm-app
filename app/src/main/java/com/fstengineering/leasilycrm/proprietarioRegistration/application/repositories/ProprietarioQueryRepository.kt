package com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel

interface ProprietarioQueryRepository {
    suspend fun getById(proprietarioId: Long): AppResult<ProprietarioReadModel, ApplicationError>
}
