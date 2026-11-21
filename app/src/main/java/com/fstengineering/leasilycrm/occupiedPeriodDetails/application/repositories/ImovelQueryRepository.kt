package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.ImovelReadModel

interface ImovelQueryRepository {
    suspend fun getById(imovelId: Long): AppResult<ImovelReadModel>
}
