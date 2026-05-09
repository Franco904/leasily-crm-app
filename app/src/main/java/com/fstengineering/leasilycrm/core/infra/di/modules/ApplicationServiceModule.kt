package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.application.services.ConnectivityServiceImpl
import com.fstengineering.leasilycrm.core.application.services.FileStorageServiceImpl
import com.fstengineering.leasilycrm.core.application.services.UserSessionServiceImpl
import com.fstengineering.leasilycrm.core.application.services.interfaces.ConnectivityService
import com.fstengineering.leasilycrm.core.application.services.interfaces.FileStorageService
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import org.koin.dsl.module

val applicationServiceModule = module {
    single<ConnectivityService> { ConnectivityServiceImpl(connectivityAdapter = get()) }
    single<FileStorageService> { FileStorageServiceImpl(sharedStorageDataSource = get()) }
    single<UserSessionService> {
        UserSessionServiceImpl(
            userSessionSharedGateway = get(),
            userSessionSharedStore = get()
        )
    }
}
