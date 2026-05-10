package com.fstengineering.leasilycrm.leadTenantDetails.di

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.di.qualifiers.deleteAssistanceQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.deleteLeadTenantQualifier
import com.fstengineering.leasilycrm.core.di.qualifiers.watchLeadTenantQualifier
import com.fstengineering.leasilycrm.core.infra.utils.authCommandUseCase
import com.fstengineering.leasilycrm.core.infra.utils.authQueryFlowUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries.LeadTenantArgs
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteAssistanceUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteLeadTenantUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.WatchLeadTenantUseCase
import org.koin.dsl.module

val leadTenantDetailsUseCaseModule = module {
    authQueryFlowUseCase<QueryFlowUseCase<LeadTenantArgs, LeadTenantReadModel?>>(
        watchLeadTenantQualifier
    ) {
        WatchLeadTenantUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentPropertyQueryRepository = get(),
            stayQueryRepository = get(),
        )
    }

    authCommandUseCase<CommandUseCase<DeleteLeadTenantCommand>>(deleteLeadTenantQualifier) {
        DeleteLeadTenantUseCase(leadTenantCommandRepository = get())
    }

    authCommandUseCase<CommandUseCase<DeleteAssistanceCommand>>(deleteAssistanceQualifier) {
        DeleteAssistanceUseCase(assistanceCommandRepository = get())
    }
}
