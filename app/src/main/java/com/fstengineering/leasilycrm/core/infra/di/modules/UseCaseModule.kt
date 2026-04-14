package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import com.fstengineering.leasilycrm.core.application.useCases.GetTodayCheckInsCheckOutsToNotifyUseCaseImpl
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
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
import com.fstengineering.leasilycrm.propertyDashboard.application.useCases.WatchPropertyUseCase
import com.fstengineering.leasilycrm.propertyOccupation.application.useCases.WatchPropertyOccupationUseCase
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases.WritePropertyOwnerUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.WatchOccupiedPeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadSelectablePropertiesUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteUnavailablePeriodUseCase
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

    // Stay details

    single {
        WatchOccupiedPeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }

    single { DeleteStayUseCase(occupiedPeriodCommandRepository = get()) }

    single { RegisterStayCheckInUseCase(occupiedPeriodCommandRepository = get()) }

    single { UndoRegisterStayCheckInUseCase(occupiedPeriodCommandRepository = get()) }

    single {
        RegisterStayCheckOutUseCase(
            occupiedPeriodCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    single {
        UndoRegisterStayCheckOutUseCase(
            occupiedPeriodCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    single { WatchMustNotShowCheckOutDialogAnymoreUseCase(occupiedPeriodQueryRepository = get()) }

    single { SetMustNotShowCheckOutDialogAnymoreUseCase(occupiedPeriodCommandRepository = get()) }

    // Property dashboard

    single {
        WatchPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
            dateProvider = get(),
        )
    }

    // Property occupation

    single {
        WatchPropertyOccupationUseCase(
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }

    single<GetTodayCheckInsCheckOutsToNotifyUseCase> {
        GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
            occupiedPeriodSharedCommandRepository = get(),
            settingsSharedStore = get(),
            userSessionSharedStore = get(),
            now = AppDateTime.lazyNow(),
        )
    }

    // Stay registration

    single {
        ReadStayUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }

    single {
        ReadUnavailablePeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
        )
    }

    single {
        ReadSelectablePropertiesUseCase(
            propertyQueryRepository = get(),
            occupiedPeriodQueryRepository = get(),
        )
    }

    single { WriteStayUseCase(occupiedPeriodCommandRepository = get()) }

    single { WriteUnavailablePeriodUseCase(occupiedPeriodCommandRepository = get()) }
}
