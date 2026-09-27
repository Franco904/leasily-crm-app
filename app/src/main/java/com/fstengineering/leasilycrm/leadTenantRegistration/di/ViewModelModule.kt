package com.fstengineering.leasilycrm.leadTenantRegistration.di

import com.fstengineering.leasilycrm.core.di.qualifiers.writeLeadTenantQualifier
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.LeadTenantRegistrationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val leadTenantRegistrationViewModelModule = module {
    viewModel {
        LeadTenantRegistrationViewModel(
            writeLeadTenant = get(writeLeadTenantQualifier),
            leadTenantQueryRepository = get(),
        )
    }
}
