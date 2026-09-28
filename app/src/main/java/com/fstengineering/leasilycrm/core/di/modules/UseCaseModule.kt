package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory {
        RegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceSharedCommandRepository = get(),
        )
    }

    factory {
        UndoRegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceSharedCommandRepository = get(),
        )
    }
}
