package com.fstengineering.leasilycrm.dashboard.di

import com.fstengineering.leasilycrm.dashboard.presentation.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val dashboardViewModelModule = module {
    viewModel {
        DashboardViewModel(userAnalyticsRepository = get())
    }
}
