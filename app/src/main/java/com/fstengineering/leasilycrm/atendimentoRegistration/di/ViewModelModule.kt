package com.fstengineering.leasilycrm.atendimentoRegistration.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.AtendimentoRegistrationViewModel
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep3ViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val atendimentoRegistrationViewModelModule = module {
    viewModel {
        AtendimentoRegistrationViewModel(
            route = get<SavedStateHandle>().toRoute(),
            atendimentoRegistrationReadDataSource = get(),
            clienteSharedReadDataSource = get(),
            atendimentoWriteDataSource = get(),
        )
    }

    viewModel { AtendimentoRegistrationStep1ViewModel() }

    viewModel { AtendimentoRegistrationStep2ViewModel(imovelReadDataSource = get()) }

    viewModel { AtendimentoRegistrationStep3ViewModel() }
}
