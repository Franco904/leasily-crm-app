package com.fstengineering.leasilycrm.leadTenantDetails.di

import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteAssistanceUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteLeadTenantUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.WatchLeadTenantUseCase
import org.koin.dsl.module

val leadTenantDetailsUseCaseModule = module {
    factory {
        WatchLeadTenantUseCase(
            leadTenantQueryRepository = get(),
            assistanceQueryRepository = get(),
            assistanceSentImovelQueryRepository = get(),
            stayQueryRepository = get(),
        )
    }

    factory {
        DeleteLeadTenantUseCase(leadTenantCommandRepository = get())
    }

    factory {
        DeleteAssistanceUseCase(assistanceCommandRepository = get())
    }
}
