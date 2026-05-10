package com.fstengineering.leasilycrm.propertyOccupation.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.watchPropertyOccupationQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authQueryFlowUseCase
import com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos.PropertyOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.propertyOccupation.application.models.queries.PropertyOccupationArgs
import com.fstengineering.leasilycrm.propertyOccupation.application.useCases.WatchPropertyOccupationUseCase
import org.koin.dsl.module

val propertyOccupationUseCaseModule = module {
    authQueryFlowUseCase<QueryFlowUseCase<PropertyOccupationArgs, PropertyOccupiedPeriodsReadModel>>(
        watchPropertyOccupationQualifier
    ) {
        WatchPropertyOccupationUseCase(
            occupiedPeriodQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }
}
