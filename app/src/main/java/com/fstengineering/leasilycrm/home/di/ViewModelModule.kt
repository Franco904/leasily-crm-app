package com.fstengineering.leasilycrm.home.di

import com.fstengineering.leasilycrm.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeViewModelModule = module {
    viewModel {
        HomeViewModel(
            occupiedPeriodSharedQueryRepository = get(),
            userQueryRepository = get(),
        )
    }
}
