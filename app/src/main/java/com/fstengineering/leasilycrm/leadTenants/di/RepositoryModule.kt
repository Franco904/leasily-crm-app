package com.fstengineering.leasilycrm.leadTenants.di

import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenants.infra.repositories.LeadTenantCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenants.infra.repositories.LeadTenantQueryRepositoryImpl
import org.koin.dsl.module

val leadTenantsRepositoryModule = module {
    single<LeadTenantQueryRepository> {
        LeadTenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
            stayDatabaseDataSource = get(),
            assistanceDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<LeadTenantCommandRepository> {
        LeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            tenantDatabaseDataSource = get(),
        )
    }
}
