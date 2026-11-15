package com.fstengineering.leasilycrm.imovelOcupacao.di

import com.fstengineering.leasilycrm.imovelOcupacao.application.useCases.WatchImovelOcupacaoUseCase
import org.koin.dsl.module

val imovelOcupacaoUseCaseModule = module {
    factory {
        WatchImovelOcupacaoUseCase(
            imovelOcupacaoReadDataSource = get(),
            leadTenantSharedQueryRepository = get(),
        )
    }
}
