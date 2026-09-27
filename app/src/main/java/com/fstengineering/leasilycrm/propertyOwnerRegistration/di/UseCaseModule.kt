package com.fstengineering.leasilycrm.propertyOwnerRegistration.di

import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases.WritePropertyOwnerUseCase
import org.koin.dsl.module

val propertyOwnerRegistrationUseCaseModule = module {
    factory {
        WritePropertyOwnerUseCase(propertyOwnerCommandRepository = get())
    }
}
