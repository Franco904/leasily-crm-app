package com.fstengineering.leasilycrm.imoveis.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ImovelQueryRepository {
    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<ImovelReadModel>, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<ImovelReadModel>, ApplicationError>>
}
