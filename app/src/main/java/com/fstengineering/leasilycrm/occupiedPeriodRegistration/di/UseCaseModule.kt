package com.fstengineering.leasilycrm.occupiedPeriodRegistration.di

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadSelectablePropertiesUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteUnavailablePeriodUseCase
import org.koin.dsl.module

val occupiedPeriodRegistrationUseCaseModule = module {
    factory {
        ReadStayUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
            tenantQueryRepository = get(),
        )
    }

    factory {
        ReadUnavailablePeriodUseCase(
            occupiedPeriodQueryRepository = get(),
            propertyQueryRepository = get(),
        )
    }

    factory {
        ReadSelectablePropertiesUseCase(
            propertyQueryRepository = get(),
            occupiedPeriodQueryRepository = get(),
        )
    }

    factory {
        WriteStayUseCase(occupiedPeriodCommandRepository = get())
    }

    factory {
        WriteUnavailablePeriodUseCase(occupiedPeriodCommandRepository = get())
    }
}
