package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.application.useCases.GetStaysStructuredDatesUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.GetTodayCheckInsCheckOutsToNotifyUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.WatchPropertyWithStaysTenantsUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.application.useCases.proxies.RequireAuthUseCaseProxy
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.userCreciNumberUseCaseQualifier
import com.fstengineering.leasilycrm.profile.application.models.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<UseCase<UserCommandModel, Unit>>(userCreciNumberUseCaseQualifier) {
        WriteUserUseCase(
            userRepository = get(),
        )
    }

    factory<UseCase<UserCommandModel, Unit>> {
        RequireAuthUseCaseProxy(
            delegate = get(userCreciNumberUseCaseQualifier),
            userSessionService = get()
        )
    }

    single<RegisterStayCheckOutUseCase> {
        RegisterStayCheckOutUseCaseImpl(
            stayRepository = get(),
            assistanceRepository = get(),
        )
    }

    single<UndoRegisterStayCheckOutUseCase> {
        UndoRegisterStayCheckOutUseCaseImpl(
            stayRepository = get(),
            assistanceRepository = get(),
        )
    }

    single<GetStaysStructuredDatesUseCase> {
        GetStaysStructuredDatesUseCaseImpl()
    }

    single<WatchPropertyWithStaysTenantsUseCase> {
        WatchPropertyWithStaysTenantsUseCaseImpl(
            propertyRepository = get(),
            tenantRepository = get(),
        )
    }

    single<GetTodayCheckInsCheckOutsToNotifyUseCase> {
        GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
            stayRepository = get(),
            settingsRepository = get(),
            authRepository = get(),
            now = AppDateTime.lazyNow(),
        )
    }
}
