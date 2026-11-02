package com.fstengineering.leasilycrm.imovelDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import kotlinx.coroutines.flow.Flow

interface ImovelQueryRepository {
    fun watchById(imovelId: Long): Flow<AppResult<ImovelReadModel, ApplicationError>>
}
