package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.application.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.core.application.di.qualifiers.plainPreferencesQualifier
import com.fstengineering.leasilycrm.core.data.repositories.UserRepositoryImpl
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> {
        UserRepositoryImpl(
            userApi = get(),
            encryptedPreferencesStorage = get(encryptedPreferencesQualifier),
            plainPreferencesStorage = get(plainPreferencesQualifier),
        )
    }
}
