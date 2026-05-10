package com.fstengineering.leasilycrm.signin.di

import com.fstengineering.leasilycrm.signin.application.useCases.SignInUseCase
import org.koin.dsl.module

val signinUseCaseModule = module {
    single { SignInUseCase(userSessionGateway = get(), userSessionStore = get()) }
}
