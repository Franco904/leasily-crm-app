package com.fstengineering.leasilycrm.leadTenantDetails.di

import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.AssistanceSentPropertyQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.LeadTenantCommandRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.LeadTenantQueryRepositoryImpl
import com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories.StayQueryRepositoryImpl
import org.koin.dsl.module

val leadTenantDetailsRepositoryModule = module {
    single<LeadTenantQueryRepository> {
        LeadTenantQueryRepositoryImpl(
            tenantDatabaseDataSource = get(),
        )
    }

    single<LeadTenantCommandRepository> {
        LeadTenantCommandRepositoryImpl(
            databaseAdapter = get(),
            tenantDatabaseDataSource = get(),
        )
    }

    single<AssistanceQueryRepository> {
        AssistanceQueryRepositoryImpl(
            assistanceDatabaseDataSource = get(),
        )
    }

    single<AssistanceCommandRepository> {
        AssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
        )
    }

    single<AssistanceSentPropertyQueryRepository> {
        AssistanceSentPropertyQueryRepositoryImpl(
            propertyDatabaseDataSource = get(),
        )
    }

    single<StayQueryRepository> {
        StayQueryRepositoryImpl(
            stayDatabaseDataSource = get(),
        )
    }
}
