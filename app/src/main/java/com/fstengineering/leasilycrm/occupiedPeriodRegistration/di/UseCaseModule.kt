package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.readSelectablePropertiesQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.readStayQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.readUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeStayQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeUnavailablePeriodQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.PropertyReadModel
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
import kotlinx.collections.immutable.ImmutableList
import org.koin.dsl.module

val occupiedPeriodRegistrationUseCaseModule = module {
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

    authQueryUseCase<QueryUseCase<SelectablePropertiesArgs, ImmutableList<PropertyReadModel>>>(
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
}
