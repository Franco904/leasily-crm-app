package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceCommandModel
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainException
import com.fstengineering.leasilycrm.core.domain.utils.SystemError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.persistentListOf

class WriteAssistanceUseCase(
    private val assistanceRepository: AssistanceRepository,
) : CommandUseCase<AssistanceCommandModel> {
    override suspend fun invoke(
        command: AssistanceCommandModel,
    ): AppResultCompound<Unit, SystemError> {
        val assistance = command.toDomain()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        assistanceRepository.insertOrUpdate(assistance)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
