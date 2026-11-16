package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import org.koin.dsl.module

val assistanceRegistrationUseCaseModule = module {
    factory {
        ReadAssistanceUseCase(
            clienteSharedReadDataSource = get(),
            assistanceQueryRepository = get(),
            assistanceSentImovelQueryRepository = get(),
        )
    }

    factory {
        WriteAssistanceUseCase(assistanceCommandRepository = get())
    }
}
