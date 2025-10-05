package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.domain.useCases.GetStaysStructuredDatesUseCaseImpl
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import org.koin.dsl.module

val utilsModule = module {
    single<GetStaysStructuredDatesUseCase> { GetStaysStructuredDatesUseCaseImpl() }
}
