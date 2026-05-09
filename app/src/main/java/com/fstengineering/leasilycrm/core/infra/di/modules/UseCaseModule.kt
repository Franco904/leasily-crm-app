package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.deleteAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.deleteLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readPropertyQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readSelectablePropertiesQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readStayQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.readUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.registerStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.undoRegisterStayCheckOutQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.watchLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.watchOccupiedPeriodQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.watchPropertyOccupationQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.watchPropertyQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeAssistanceQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writePropertyOwnerQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writePropertyQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeStayQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.core.infra.di.qualifiers.writeUserQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryFlowUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries.LeadTenantArgs
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteAssistanceUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteLeadTenantUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.WatchLeadTenantUseCase
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases.WriteLeadTenantUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries.OccupiedPeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.WatchOccupiedPeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectablePropertiesArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.StayArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadSelectablePropertiesUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import com.fstengineering.leasilycrm.propertyDashboard.application.useCases.WatchPropertyUseCase
import com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos.PropertyOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.propertyOccupation.application.models.queries.PropertyOccupationArgs
import com.fstengineering.leasilycrm.propertyOccupation.application.useCases.WatchPropertyOccupationUseCase
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.useCases.WritePropertyOwnerUseCase
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.ReadPropertyUseCase
import com.fstengineering.leasilycrm.propertyRegistration.application.useCases.WritePropertyUseCase
import com.fstengineering.leasilycrm.signin.application.useCases.SignInUseCase
import kotlinx.collections.immutable.ImmutableList
import org.koin.dsl.module
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.PropertyReadModel as StayRegPropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel as PropertyDashboardReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.queries.PropertyArgs as PropertyDashboardArgs
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyReadModel as PropertyRegistrationReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.queries.PropertyArgs as PropertyRegistrationArgs

val useCaseModule = module {
    // Assistance registration

    authQueryUseCase<QueryUseCase<AssistanceArgs, AssistanceReadModel>>(readAssistanceQualifier) {
        ReadAssistanceUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<AssistanceCommandModel>>(writeAssistanceQualifier) {
        WriteAssistanceUseCase(assistanceCommandRepository = get())
    }

    // Lead/tenant registration

    authCommandUseCase<CommandUseCase<LeadTenantCommandModel>>(writeLeadTenantQualifier) {
        WriteLeadTenantUseCase(leadTenantCommandRepository = get())
    }

    // Property owner registration

    authCommandUseCase<CommandUseCase<PropertyOwnerCommandModel>>(writePropertyOwnerQualifier) {
        WritePropertyOwnerUseCase(propertyOwnerCommandRepository = get())
    }

    // Profile

    authCommandUseCase<CommandUseCase<UserCommandModel>>(writeUserQualifier) {
        WriteUserUseCase(repository = get())
    }

    // Sign in (excluded from auth-wrapping — bootstraps authentication itself)

    single { SignInUseCase(userSessionGateway = get(), userSessionStore = get()) }

    // Stay details

    authQueryFlowUseCase<QueryFlowUseCase<OccupiedPeriodArgs, OccupiedPeriodReadModel?>>(
        watchOccupiedPeriodQualifier
    ) {
        WatchOccupiedPeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }

    // Core check-out use cases

    authCommandUseCase<CommandUseCase<RegisterCheckOutCommand>>(registerStayCheckOutQualifier) {
        RegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<UndoCheckOutCommand>>(undoRegisterStayCheckOutQualifier) {
        UndoRegisterStayCheckOutUseCase(
            occupiedPeriodSharedCommandRepository = get(),
            assistanceCommandRepository = get(),
        )
    }

    // Property dashboard

    authQueryFlowUseCase<QueryFlowUseCase<PropertyDashboardArgs, PropertyDashboardReadModel>>(
        watchPropertyQualifier
    ) {
        WatchPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
            dateProvider = get(),
        )
    }

    // Property occupation

    authQueryFlowUseCase<QueryFlowUseCase<PropertyOccupationArgs, PropertyOccupiedPeriodsReadModel>>(
        watchPropertyOccupationQualifier
    ) {
        WatchPropertyOccupationUseCase(
            occupiedPeriodQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }

    // Stay registration

    authQueryUseCase<QueryUseCase<StayArgs, StayReadModel>>(readStayQualifier) {
        ReadStayUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }

    authQueryUseCase<QueryUseCase<UnavailablePeriodArgs, UnavailablePeriodReadModel>>(
        readUnavailablePeriodQualifier
    ) {
        ReadUnavailablePeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
        )
    }

    authQueryUseCase<QueryUseCase<SelectablePropertiesArgs, ImmutableList<StayRegPropertyReadModel>>>(
        readSelectablePropertiesQualifier
    ) {
        ReadSelectablePropertiesUseCase(
            propertyQueryRepository = get(),
            occupiedPeriodQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<StayCommandModel>>(writeStayQualifier) {
        WriteStayUseCase(occupiedPeriodCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<UnavailablePeriodCommandModel>>(
        writeUnavailablePeriodQualifier
    ) {
        WriteUnavailablePeriodUseCase(occupiedPeriodCommandRepository = get())
    }

    // Lead tenant details

    authQueryFlowUseCase<QueryFlowUseCase<LeadTenantArgs, LeadTenantReadModel?>>(
        watchLeadTenantQualifier
    ) {
        WatchLeadTenantUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
            stayQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<DeleteLeadTenantCommand>>(deleteLeadTenantQualifier) {
        DeleteLeadTenantUseCase(leadTenantCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<DeleteAssistanceCommand>>(deleteAssistanceQualifier) {
        DeleteAssistanceUseCase(assistanceCommandRepository = get())
    }

    // Property registration

    authQueryUseCase<QueryUseCase<PropertyRegistrationArgs, PropertyRegistrationReadModel>>(
        readPropertyQualifier
    ) {
        ReadPropertyUseCase(propertyQueryRepository = get(), propertyOwnerQueryRepository = get())
    }

    authCommandUseCase<CommandUseCase<PropertyCommandModel>>(writePropertyQualifier) {
        WritePropertyUseCase(propertyCommandRepository = get())
    }
}
