package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryUseCase
import org.koin.dsl.module

val assistanceRegistrationUseCaseModule = module {
    authQueryUseCase<QueryUseCase<AssistanceArgs, AssistanceReadModel>>(readAssistanceQualifier) {
        ReadAssistanceUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<AssistanceCommandModel>>(writeAssistanceQualifier) {
        WriteAssistanceUseCase(assistanceCommandRepository = get())
    }
}
