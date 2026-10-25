package com.fstengineering.leasilycrm.proprietarioRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.proprietarioRegistration.application.mappers.toProprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource

class WriteProprietarioUseCase(
    private val proprietarioWriteDataSource: ProprietarioWriteDataSource,
) {
    suspend operator fun invoke(
        command: ProprietarioCommandModel,
    ): AppResult<Unit, BaseError> {
        val proprietario = command.toProprietario()
            .fold(onError = { return AppResult.Error(error = it) }, onSuccess = { it })

        return proprietarioWriteDataSource.save(proprietario)
    }
}
