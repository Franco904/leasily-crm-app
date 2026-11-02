package com.fstengineering.leasilycrm.imovelOccupation.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedPeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface OccupiedPeriodQueryRepository {
    fun watchAllByImovelId(
        imovelId: Long,
    ): Flow<AppResult<ImmutableList<ImovelOccupiedPeriodRawReadModel>, ApplicationError>>
}
