package com.fstengineering.leasilycrm.proprietarios.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarios.application.models.dtos.ProprietarioReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProprietarioQueryRepository {
    fun watchAll(): Flow<AppResult<ImmutableList<ProprietarioReadModel>, ApplicationError>>
}
