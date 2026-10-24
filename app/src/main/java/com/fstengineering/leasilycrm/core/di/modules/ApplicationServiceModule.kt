package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.services.ConnectivityServiceImpl
import com.fstengineering.leasilycrm.core.application.services.interfaces.ConnectivityService
import com.fstengineering.leasilycrm.core.application.services.UserSessionService
import org.koin.dsl.module

val applicationServiceModule = module {
    single<ConnectivityService> { ConnectivityServiceImpl(connectivityAdapter = get()) }
    single {
        UserSessionService(
            userSessionSharedStore = get(),
            userRemoteDataSource = get(),
        )
    }
}
