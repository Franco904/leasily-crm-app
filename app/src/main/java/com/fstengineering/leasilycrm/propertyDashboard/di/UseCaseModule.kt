package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.watchPropertyQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authQueryFlowUseCase
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyDashboard.application.useCases.WatchPropertyUseCase
import org.koin.dsl.module

val propertyDashboardUseCaseModule = module {
    authQueryFlowUseCase<QueryFlowUseCase<PropertyArgs, PropertyReadModel>>(watchPropertyQualifier) {
        WatchPropertyUseCase(
            propertyQueryRepository = get(),
            propertyOwnerQueryRepository = get(),
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
            dateProvider = get(),
        )
    }
}
