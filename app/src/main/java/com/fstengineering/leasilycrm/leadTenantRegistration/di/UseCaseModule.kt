package com.fstengineering.leasilycrm.leadTenantRegistration.di

import com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases.WriteLeadTenantUseCase
import org.koin.dsl.module

val leadTenantRegistrationUseCaseModule = module {
    factory {
        WriteLeadTenantUseCase(leadTenantCommandRepository = get())
    }
}
