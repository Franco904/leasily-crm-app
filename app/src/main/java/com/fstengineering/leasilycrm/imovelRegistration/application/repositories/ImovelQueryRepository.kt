package com.fstengineering.leasilycrm.imovelRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface ImovelQueryRepository {
    suspend fun getById(imovelId: Long): AppResult<ImovelRawReadModel, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError>
}