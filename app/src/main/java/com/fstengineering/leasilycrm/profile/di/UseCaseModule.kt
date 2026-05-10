package com.fstengineering.leasilycrm.profile.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.writeUserQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import org.koin.dsl.module

val profileUseCaseModule = module {
    authCommandUseCase<CommandUseCase<UserCommandModel>>(writeUserQualifier) {
        WriteUserUseCase(repository = get())
    }
}
