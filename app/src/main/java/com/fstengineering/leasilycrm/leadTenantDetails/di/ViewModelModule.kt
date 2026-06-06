package com.fstengineering.leasilycrm.leadTenantDetails.di

import com.fstengineering.leasilycrm.core.di.qualifiers.deleteAssistanceQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.deleteLeadTenantQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.watchLeadTenantQualifier
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.LeadTenantDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val leadTenantDetailsViewModelModule = module {
    viewModel {
        LeadTenantDetailsViewModel(
            watchLeadTenant = get(watchLeadTenantQualifier),
            deleteLeadTenant = get(deleteLeadTenantQualifier),
            deleteAssistance = get(deleteAssistanceQualifier),
        )
    }
}
