package com.fstengineering.leasilycrm.propertyRegistration.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.readPropertyQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writePropertyQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryUseCase
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.ReadPropertyUseCase
import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.WritePropertyUseCase
import org.koin.dsl.module

val propertyRegistrationUseCaseModule = module {
    authQueryUseCase<QueryUseCase<PropertyArgs, PropertyReadModel>>(readPropertyQualifier) {
        ReadPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<PropertyCommandModel>>(writePropertyQualifier) {
        WritePropertyUseCase(propertyCommandRepository = get())
    }
}
