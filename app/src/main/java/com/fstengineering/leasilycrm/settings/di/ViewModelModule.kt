package com.fstengineering.leasilycrm.settings.di

import com.fstengineering.leasilycrm.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {
    viewModel {
        SettingsViewModel(settingsSharedStore = get())
    }
}
