package com.fstengineering.leasilycrm.profile.di

import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository
import com.fstengineering.leasilycrm.profile.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.profile.infra.repositories.UserCommandRepositoryImpl
import com.fstengineering.leasilycrm.profile.infra.repositories.UserQueryRepositoryImpl
import org.koin.dsl.module

val profileRepositoryModule = module {
    single<UserCommandRepository> {
        UserCommandRepositoryImpl(
            remoteDataSource = get(),
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }

    single<UserQueryRepository> {
        UserQueryRepositoryImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }
}
