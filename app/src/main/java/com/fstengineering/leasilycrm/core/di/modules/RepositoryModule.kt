package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.AppTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppTimeProvider
import com.fstengineering.leasilycrm.core.infra.repositories.OccupiedPeriodSharedCommandRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.repositories.OccupiedPeriodSharedQueryRepositoryImpl
import com.fstengineering.leasilycrm.core.infra.stores.SettingsSharedStoreImpl
import com.fstengineering.leasilycrm.core.infra.stores.UserSessionSharedStoreImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<OccupiedPeriodSharedCommandRepository> {
        OccupiedPeriodSharedCommandRepositoryImpl(
            databaseAdapter = get(),
            occupiedPeriodDatabaseDataSource = get(),
            plainLocalStorage = get(plainPreferencesQualifier),
        )
    }

    single<OccupiedPeriodSharedQueryRepository> {
        OccupiedPeriodSharedQueryRepositoryImpl(
            occupiedPeriodDatabaseDataSource = get(),
        )
    }

    single<SettingsSharedStore> {
        SettingsSharedStoreImpl(
            plainLocalStorage = get(plainPreferencesQualifier),
        )
    }

    single<UserSessionSharedStore> {
        UserSessionSharedStoreImpl(
            encryptedLocalStorage = get(encryptedPreferencesQualifier),
        )
    }

    single<AppDateProvider> { AppDateProviderImpl() }
    single<AppDateTimeProvider> { AppDateTimeProviderImpl() }
    single<AppTimeProvider> { AppTimeProviderImpl() }
}
