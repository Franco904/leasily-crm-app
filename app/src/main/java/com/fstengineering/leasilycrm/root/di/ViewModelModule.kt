package com.fstengineering.leasilycrm.root.di

import com.fstengineering.leasilycrm.root.RootViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val rootViewModelModule = module {
    viewModel {
        RootViewModel(
            userSessionSharedStore = get(),
            settingsSharedStore = get(),
        )
    }
}
