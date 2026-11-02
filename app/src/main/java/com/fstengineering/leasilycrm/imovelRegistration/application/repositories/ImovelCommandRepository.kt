package com.fstengineering.leasilycrm.imovelRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel

interface ImovelCommandRepository {
    suspend fun insertOrUpdate(imovel: Imovel): AppResult<Unit, ApplicationError>
}