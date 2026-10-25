package com.fstengineering.leasilycrm.proprietarioRegistration.di

import com.fstengineering.leasilycrm.proprietarioRegistration.application.useCases.WriteProprietarioUseCase
import org.koin.dsl.module

val proprietarioRegistrationUseCaseModule = module {
    factory {
        WriteProprietarioUseCase(proprietarioCommandRepository = get())
    }
}
