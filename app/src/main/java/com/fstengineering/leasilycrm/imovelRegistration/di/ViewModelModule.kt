package com.fstengineering.leasilycrm.imovelRegistration.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationViewModel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels.ImovelRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels.ImovelRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels.ImovelRegistrationStep3ViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelRegistrationViewModelModule = module {
    viewModel {
        ImovelRegistrationViewModel(
            route = get<SavedStateHandle>().toRoute(),
            imovelRegistrationReadDataSource = get(),
            imovelWriteDataSource = get(),
            proprietarioReadDataSource = get(),
        )
    }

    viewModel { ImovelRegistrationStep1ViewModel() }

    viewModel { ImovelRegistrationStep2ViewModel() }

    viewModel { ImovelRegistrationStep3ViewModel() }
}
