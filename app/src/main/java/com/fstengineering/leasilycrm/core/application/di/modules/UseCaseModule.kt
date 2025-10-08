package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.domain.useCases.DeleteCurrentAssistanceUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.DeleteStayUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.GetStaysStructuredDatesUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.RegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.UndoRegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.WatchPropertyWithStaysTenantsUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteCurrentAssistanceUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteStayUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single<DeleteCurrentAssistanceUseCase> {
        DeleteCurrentAssistanceUseCaseImpl(
            assistanceRepository = get(),
            tenantRepository = get(),
        )
    }

    single<DeleteStayUseCase> {
        DeleteStayUseCaseImpl(
            stayRepository = get(),
            tenantRepository = get(),
        )
    }

    single<RegisterStayCheckOutUseCase> {
        RegisterStayCheckOutUseCaseImpl(
            stayRepository = get(),
            assistanceRepository = get(),
            tenantRepository = get(),
        )
    }

    single<UndoRegisterStayCheckOutUseCase> {
        UndoRegisterStayCheckOutUseCaseImpl(
            stayRepository = get(),
            assistanceRepository = get(),
            tenantRepository = get(),
        )
    }

    single<GetStaysStructuredDatesUseCase> {
        GetStaysStructuredDatesUseCaseImpl()
    }

    single<WatchPropertyWithStaysTenantsUseCase> {
        WatchPropertyWithStaysTenantsUseCaseImpl(
            propertyRepository = get(),
            tenantRepository = get(),
            applicationScope = get(),
        )
    }
}
