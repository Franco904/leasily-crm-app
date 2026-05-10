package com.fstengineering.leasilycrm.signin.di

import com.fstengineering.leasilycrm.signin.presentation.SignInViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val signinViewModelModule = module {
    viewModel {
        SignInViewModel(
            signIn = get(),
            connectivityService = get(),
        )
    }
}
