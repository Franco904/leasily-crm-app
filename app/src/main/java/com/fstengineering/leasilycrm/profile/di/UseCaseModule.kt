package com.fstengineering.leasilycrm.profile.di

import com.fstengineering.leasilycrm.profile.application.useCases.ClearGoogleCredentialStateUseCase
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import org.koin.dsl.module

val profileUseCaseModule = module {
    factory {
        WriteUserUseCase(repository = get())
    }

    factory {
        ClearGoogleCredentialStateUseCase(credentialManager = get())
    }
}
