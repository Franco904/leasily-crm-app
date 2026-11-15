package com.fstengineering.leasilycrm.imovelDashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface StayQueryRepository {
    fun watchAllByImovelId(
        imovelId: Long,
    ): Flow<AppResult<ImmutableList<ImovelPeriodoOcupadoRawReadModel>, ApplicationError>>
}
