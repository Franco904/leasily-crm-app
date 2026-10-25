package com.fstengineering.leasilycrm.propertyRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.ProprietarioReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProprietarioQueryRepository {
    suspend fun getById(proprietarioId: Long): AppResult<ProprietarioReadModel, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<ProprietarioReadModel>, ApplicationError>>
}
