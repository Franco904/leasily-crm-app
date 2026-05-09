package com.fstengineering.leasilycrm.assistanceRegistration.application.useCases

import com.fstengineering.leasilycrm.assistanceRegistration.application.mappers.toEntity
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.BusinessRuleError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.persistentListOf

class WriteAssistanceUseCase(
    private val assistanceCommandRepository: AssistanceCommandRepository,
) : CommandUseCase<AssistanceCommandModel> {
    override suspend fun invoke(
        command: AssistanceCommandModel,
    ): AppResultCompound<Unit, BusinessRuleError> {
        val assistance = command.toEntity()
            .fold(onErrors = { return AppResultCompound.Errors(it) }, onSuccess = { it })

        assistanceCommandRepository.insertOrUpdate(assistance)
            .onError { e -> return AppResultCompound.Errors(errors = persistentListOf(e)) }

        return AppResultCompound.Success(data = Unit)
    }
}
