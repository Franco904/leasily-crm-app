package com.fstengineering.leasilycrm.leadTenantRegistration.di

import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories.LeadTenantCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories.LeadTenantQueryRepositoryImpl
import org.koin.dsl.module

val leadTenantRegistrationRepositoryModule = module {
    single<LeadTenantCommandRepository> {
        LeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            databaseDataSource = get(),
        )
    }

    single<LeadTenantQueryRepository> {
        LeadTenantQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }
}
