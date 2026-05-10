package com.fstengineering.leasilycrm.propertyOwnerRegistration.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.writePropertyOwnerQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases.WritePropertyOwnerUseCase
import org.koin.dsl.module

val propertyOwnerRegistrationUseCaseModule = module {
    authCommandUseCase<CommandUseCase<PropertyOwnerCommandModel>>(writePropertyOwnerQualifier) {
        WritePropertyOwnerUseCase(propertyOwnerCommandRepository = get())
    }
}
