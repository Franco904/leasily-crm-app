package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.ImovelReadModel
import kotlinx.collections.immutable.ImmutableList

interface ImovelQueryRepository {
    suspend fun getById(imovelId: Long): AppResult<ImovelReadModel, ApplicationError>

    suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<ImovelReadModel>, ApplicationError>
}
