package com.fstengineering.leasilycrm.leadTenants.di

import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenants.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.LeadsViewModel
import com.fstengineering.leasilycrm.leadTenants.presentation.tenants.TenantsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val leadTenantsViewModelModule = module {
    viewModel {
        LeadsViewModel(
            leadTenantQueryRepository = get<LeadTenantQueryRepository>(),
            leadTenantCommandRepository = get<LeadTenantCommandRepository>(),
        )
    }

    viewModel {
        TenantsViewModel(
            leadTenantQueryRepository = get<LeadTenantQueryRepository>(),
            leadTenantCommandRepository = get<LeadTenantCommandRepository>(),
        )
    }
}
