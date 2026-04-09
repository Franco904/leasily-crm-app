package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import com.fstengineering.leasilycrm.core.application.useCases.GetStaysStructuredDatesUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.GetTodayCheckInsCheckOutsToNotifyUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.WatchPropertyWithStaysTenantsUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writePropertyOwnerQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeUserQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryUseCase
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases.WriteLeadTenantUseCase
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases.WritePropertyOwnerUseCase
import org.koin.dsl.module

val useCaseModule = module {
    authQueryUseCase<QueryUseCase<AssistanceArgs, AssistanceReadModel>>(
        readAssistanceQualifier,
    ) {
        ReadAssistanceUseCase(
            tenantRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<AssistanceCommandModel>>(writeAssistanceQualifier) {
        WriteAssistanceUseCase(assistanceCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<LeadTenantCommandModel>>(writeLeadTenantQualifier) {
        WriteLeadTenantUseCase(leadTenantCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<PropertyOwnerCommandModel>>(writePropertyOwnerQualifier) {
        WritePropertyOwnerUseCase(propertyOwnerCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<UserCommandModel>>(writeUserQualifier) {
        WriteUserUseCase(repository = get())
    }

    single<RegisterStayCheckOutUseCase> {
        RegisterStayCheckOutUseCaseImpl(
            staySharedCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    single<UndoRegisterStayCheckOutUseCase> {
        UndoRegisterStayCheckOutUseCaseImpl(
            staySharedCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    single<GetStaysStructuredDatesUseCase> {
        GetStaysStructuredDatesUseCaseImpl()
    }

    single<WatchPropertyWithStaysTenantsUseCase> {
        WatchPropertyWithStaysTenantsUseCaseImpl(
            propertyRepository = get(),
            leadTenantSharedCommandRepository = get(),
        )
    }

    single<GetTodayCheckInsCheckOutsToNotifyUseCase> {
        GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
            staySharedCommandRepository = get(),
            settingsSharedQueryRepository = get(),
            userSessionSharedGateway = get(),
            now = AppDateTime.lazyNow(),
        )
    }
}
