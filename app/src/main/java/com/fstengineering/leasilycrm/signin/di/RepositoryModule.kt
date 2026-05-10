package com.fstengineering.leasilycrm.signin.di

import com.fstengineering.leasilycrm.core.di.qualifiers.encryptedPreferencesQualifier
import com.fstengineering.leasilycrm.signin.application.gateways.UserSessionGateway
import com.fstengineering.leasilycrm.signin.application.stores.UserSessionStore
import com.fstengineering.leasilycrm.signin.infra.gateways.UserSessionGatewayImpl
import com.fstengineering.leasilycrm.signin.infra.stores.UserSessionStoreImpl
import org.koin.dsl.module

val signinRepositoryModule = module {
    single<UserSessionGateway> {
        UserSessionGatewayImpl(remoteDataSource = get())
    }

    single<UserSessionStore> {
        UserSessionStoreImpl(encryptedPreferencesDataSource = get(encryptedPreferencesQualifier))
    }
}
