package com.fstengineering.leasilycrm.propertyRegistration.di

import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.ReadPropertyUseCase
import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.WritePropertyUseCase
import org.koin.dsl.module

val propertyRegistrationUseCaseModule = module {
    factory {
        ReadPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
        )
    }

    factory {
        WritePropertyUseCase(propertyCommandRepository = get())
    }
}
