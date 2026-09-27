package com.fstengineering.leasilycrm.propertyDashboard.di

import com.fstengineering.leasilycrm.core.di.qualifiers.watchPropertyQualifier
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val propertyDashboardViewModelModule = module {
    viewModel {
        PropertyDashboardViewModel(
            watchProperty = get(watchPropertyQualifier),
            propertySharedCommandRepository = get(),
        )
    }
}
