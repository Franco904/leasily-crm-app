package com.fstengineering.leasilycrm.imovelRegistration.application.useCases

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ProprietarioReadDataSource

class ReadImovelUseCase(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val proprietarioReadDataSource: ProprietarioReadDataSource,
) {
    suspend operator fun invoke(
        args: ImovelArgs,
    ): AppResult<ImovelReadModel, ApplicationError> {
        val imovelRaw = imovelQueryRepository.getById(args.id)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedImovel -> storedImovel },
            )

        val proprietario = proprietarioReadDataSource.getProprietario(imovelRaw.ownerId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { storedProprietario -> storedProprietario },
            )

        val updatedReadModel = ImovelReadModel.create(
            owner = proprietario,
            imovelRaw = imovelRaw,
        )

        return AppResult.Success(data = updatedReadModel)
    }
}
