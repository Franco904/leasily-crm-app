package com.fstengineering.leasilycrm.profile.di

import com.fstengineering.leasilycrm.core.di.qualifiers.writeUserQualifier
import com.fstengineering.leasilycrm.profile.presentation.ProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileViewModelModule = module {
    viewModel {
        ProfileViewModel(
            writeUser = get(writeUserQualifier),
            connectivityService = get(),
            userSessionService = get(),
            userQueryRepository = get(),
        )
    }
}
