package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.application.services.UserSessionServiceImpl
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import org.koin.dsl.module

val applicationServiceModule = module {
    single<UserSessionService> { UserSessionServiceImpl(authRepository = get()) }
}
