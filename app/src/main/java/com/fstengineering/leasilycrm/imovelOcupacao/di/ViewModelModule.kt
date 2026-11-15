package com.fstengineering.leasilycrm.imovelOcupacao.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.ImovelOcupacaoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val imovelOcupacaoViewModelModule = module {
    viewModel {
        ImovelOcupacaoViewModel(
            route = get<SavedStateHandle>().toRoute(),
            watchImovelOcupacao = get(),
            dateProvider = get(),
        )
    }
}
