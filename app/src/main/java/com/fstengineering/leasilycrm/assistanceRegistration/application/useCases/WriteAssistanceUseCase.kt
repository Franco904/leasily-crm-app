package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.persistentListOf

class WriteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : CommandUseCase<AssistanceCommandModel> {
    override suspend fun invoke(
        command: AssistanceCommandModel,
    ): AppResultCompound<Unit, ApplicationError> {
        val assistance = command.toAggregate()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        assistanceCommandRepository.insertOrUpdate(assistance)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
