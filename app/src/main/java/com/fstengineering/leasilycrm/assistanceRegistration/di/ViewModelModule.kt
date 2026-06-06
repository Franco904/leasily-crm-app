package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.di.qualifiers.readAssistanceQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.writeAssistanceQualifier
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val assistanceRegistrationViewModelModule = module {
    viewModel {
        AssistanceRegistrationViewModel(
            assistanceQueryRepository = get(),
            readAssistance = get(readAssistanceQualifier),
            writeAssistance = get(writeAssistanceQualifier),
        )
    }

    viewModel { AssistanceRegistrationStep1ViewModel() }

    viewModel {
        AssistanceRegistrationStep2ViewModel(
            assistanceSelectablePropertyQueryRepository = get(),
        )
    }

    viewModel { AssistanceRegistrationStep3ViewModel() }
}
