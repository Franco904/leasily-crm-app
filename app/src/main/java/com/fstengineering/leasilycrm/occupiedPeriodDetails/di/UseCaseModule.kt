package com.fstengineering.leasilycrm.occupiedPeriodDetails.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.watchOccupiedPeriodQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authQueryFlowUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries.OccupiedPeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.WatchOccupiedPeriodUseCase
import org.koin.dsl.module

val occupiedPeriodDetailsUseCaseModule = module {
    authQueryFlowUseCase<QueryFlowUseCase<OccupiedPeriodArgs, OccupiedPeriodReadModel?>>(
        watchOccupiedPeriodQualifier
    ) {
        WatchOccupiedPeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }
}
