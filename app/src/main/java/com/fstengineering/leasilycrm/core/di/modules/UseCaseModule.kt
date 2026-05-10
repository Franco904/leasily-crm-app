package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.registerStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.undoRegisterStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import org.koin.dsl.module

val useCaseModule = module {
    authCommandUseCase<CommandUseCase<RegisterCheckOutCommand>>(registerStayCheckOutQualifier) {
        RegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceSharedCommandRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<UndoCheckOutCommand>>(undoRegisterStayCheckOutQualifier) {
        UndoRegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceSharedCommandRepository = get(),
        )
    }
}
