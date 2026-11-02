package com.fstengineering.leasilycrm.imovelRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.imovelRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.imovelRegistration.application.models.commands.ImovelCommandModel
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelCommandRepository

class WriteImovelUseCase(
    private val imovelCommandRepository: ImovelCommandRepository,
) {
    suspend operator fun invoke(
        command: ImovelCommandModel,
    ): AppResult<Unit, BaseError> {
        val imovel = command.toEntity()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return imovelCommandRepository.insertOrUpdate(imovel)
    }
}
