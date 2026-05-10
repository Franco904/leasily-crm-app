package com.fstengineering.leasilycrm.home.di

import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.home.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.home.infra.repositories.UserQueryRepositoryImpl
import org.koin.dsl.module

val homeRepositoryModule = module {
    single<UserQueryRepository> {
        UserQueryRepositoryImpl(
            encryptedPreferencesDataSource = get(encryptedPreferencesQualifier),
        )
    }
}
