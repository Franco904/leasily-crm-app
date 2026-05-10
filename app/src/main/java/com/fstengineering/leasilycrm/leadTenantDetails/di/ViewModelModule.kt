package com.fstengineering.leasilycrm.leadTenantDetails.di

import com.fstengineering.leasilycrm.leadTenantDetails.presentation.LeadTenantDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val leadTenantDetailsViewModelModule = module {
    viewModel {
        LeadTenantDetailsViewModel(
            watchLeadTenant = get(),
            deleteLeadTenant = get(),
            deleteAssistance = get(),
        )
    }
}
