package com.fstengineering.leasilycrm.imovelDashboard.di

import com.fstengineering.leasilycrm.imovelDashboard.application.useCases.WatchImovelUseCase
import org.koin.dsl.module

val imovelDashboardUseCaseModule = module {
    factory {
        WatchImovelUseCase(
            imovelDashboardReadDataSource = get(),
            proprietarioReadDataSource = get(),
            clienteSharedReadDataSource = get(),
            dateProvider = get(),
        )
    }
}
