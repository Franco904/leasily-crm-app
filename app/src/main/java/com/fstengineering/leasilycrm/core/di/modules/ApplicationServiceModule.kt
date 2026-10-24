package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.services.UserSessionService
import org.koin.dsl.module

val applicationServiceModule = module {
    single {
        UserSessionService(
            userSessionSharedStore = get(),
            userRemoteDataSource = get(),
        )
    }
}
