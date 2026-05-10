package com.fstengineering.leasilycrm.leadTenantRegistration.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.writeLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.useCases.WriteLeadTenantUseCase
import org.koin.dsl.module

val leadTenantRegistrationUseCaseModule = module {
    authCommandUseCase<CommandUseCase<LeadTenantCommandModel>>(writeLeadTenantQualifier) {
        WriteLeadTenantUseCase(leadTenantCommandRepository = get())
    }
}
