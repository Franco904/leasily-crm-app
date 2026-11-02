package com.fstengineering.leasilycrm.imovelDashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelRawReadModel
import kotlinx.coroutines.flow.Flow

interface ImovelQueryRepository {
    fun watchById(imovelId: Long): Flow<AppResult<ImovelRawReadModel, ApplicationError>>
}
