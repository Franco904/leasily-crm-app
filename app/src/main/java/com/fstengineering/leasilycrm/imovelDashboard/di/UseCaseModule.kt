package com.fstengineering.leasilycrm.imovelDashboard.di

import com.fstengineering.leasilycrm.imovelDashboard.application.useCases.WatchImovelUseCase
import org.koin.dsl.module

val imovelDashboardUseCaseModule = module {
    factory {
        WatchImovelUseCase(
            imovelQueryRepository = get(),
            proprietarioReadDataSource = get(),
            stayQueryRepository = get(),
            leadTenantSharedQueryRepository = get(),
            dateProvider = get(),
        )
    }
}
