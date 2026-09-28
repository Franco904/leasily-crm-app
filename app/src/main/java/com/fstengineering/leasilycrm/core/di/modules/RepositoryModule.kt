package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.repositories.AssistanceSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.infra.repositories.AssistanceSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.LeadTenantSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.OccupiedPeriodSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.OccupiedPeriodSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertyOwnerSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.PropertySharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.stores.SettingsSharedStoreImpl
import com.fstengineering.leasilycrm.core.infra.stores.UserSessionSharedStoreImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<OccupiedPeriodSharedCommandRepository> {
        OccupiedPeriodSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            occupiedPeriodDatabaseDataSource = get(),
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<OccupiedPeriodSharedQueryRepository> {
        OccupiedPeriodSharedQueryRepositoryImpl(
            occupiedPeriodDatabaseDataSource = get(),
        )
    }

    single<LeadTenantSharedCommandRepository> {
        LeadTenantSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            databaseDataSource = get(),
        )
    }

    single<LeadTenantSharedQueryRepository> {
        LeadTenantSharedQueryRepositoryImpl(
            databaseDataSource = get(),
        )
    }

    single<PropertyOwnerSharedCommandRepository> {
        PropertyOwnerSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyOwnerDatabaseDataSource = get(),
        )
    }

    single<PropertySharedCommandRepository> {
        PropertySharedCommandRepositoryImpl(
            databaseAdapter = get(),
            propertyDatabaseDataSource = get(),
        )
    }

    single<AssistanceSharedCommandRepository> {
        AssistanceSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
        )
    }

    single<SettingsSharedStore> {
        SettingsSharedStoreImpl(
            plainPreferencesDataSource = get(plainPreferencesQualifier),
        )
    }

    single<UserSessionSharedStore> {
        UserSessionSharedStoreImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<AppDateProvider> { AppDateProviderImpl() }
    single<AppDateTimeProvider> { AppDateTimeProviderImpl() }
}
