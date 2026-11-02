package com.fstengineering.leasilycrm.imovelRegistration.di

import com.fstengineering.leasilycrm.imovelRegistration.application.useCases.ReadImovelUseCase
import com.fstengineering.leasilycrm.imovelRegistration.application.useCases.WriteImovelUseCase
import org.koin.dsl.module

val imovelRegistrationUseCaseModule = module {
    factory {
        ReadImovelUseCase(
            imovelQueryRepository = get(),
            proprietarioReadDataSource = get(),
        )
    }

    factory {
        WriteImovelUseCase(imovelCommandRepository = get())
    }
}
