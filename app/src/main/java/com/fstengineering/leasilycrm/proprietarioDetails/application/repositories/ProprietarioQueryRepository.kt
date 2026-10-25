package com.fstengineering.leasilycrm.proprietarioDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import kotlinx.coroutines.flow.Flow

interface ProprietarioQueryRepository {
    fun watchById(proprietarioId: Long): Flow<AppResult<ProprietarioReadModel, ApplicationError>>
}
