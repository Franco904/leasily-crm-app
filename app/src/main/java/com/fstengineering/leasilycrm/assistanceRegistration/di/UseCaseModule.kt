package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import org.koin.dsl.module

val assistanceRegistrationUseCaseModule = module {
    factory {
        ReadAssistanceUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
        )
    }

    factory {
        WriteAssistanceUseCase(assistanceCommandRepository = get())
    }
}
