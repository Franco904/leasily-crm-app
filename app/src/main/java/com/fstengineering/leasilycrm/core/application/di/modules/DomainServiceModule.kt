package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.domain.services.UserSessionServiceImpl
import com.fstengineering.leasilycrm.core.domain.services.interfaces.UserSessionService
import org.koin.dsl.module

val domainServiceModule = module {
    single<UserSessionService> { UserSessionServiceImpl(authRepository = get()) }
}
