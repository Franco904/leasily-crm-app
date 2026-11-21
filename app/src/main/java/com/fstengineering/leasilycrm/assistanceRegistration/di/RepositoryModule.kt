package com.fstengineering.leasilycrm.assistanceRegistration.di

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSelectableImovelQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentImovelQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceCommandRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSelectableImovelQueryRepositoryImpl
import com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories.AssistanceSentImovelQueryRepositoryImpl
import org.koin.dsl.module

val assistanceRegistrationRepositoryModule = module {
    single<AssistanceCommandRepository> {
        AssistanceCommandRepositoryImpl(
            databaseAdapter = get(),
            assistanceDatabaseDataSource = get(),
            assistanceCharacteristicCrossRefDatabaseDataSource = get(),
            assistanceInteractionDatabaseDataSource = get(),
            assistanceSearchedNeighborhoodDatabaseDataSource = get(),
            assistanceSentImovelCrossRefDatabaseDataSource = get(),
        )
    }

    single<AssistanceQueryRepository> {
        AssistanceQueryRepositoryImpl(
            assistanceDatabaseDataSource = get(),
        )
    }

    single<AssistanceSentImovelQueryRepository> {
        AssistanceSentImovelQueryRepositoryImpl(
            imovelDatabaseDataSource = get(),
        )
    }

    single<AssistanceSelectableImovelQueryRepository> {
        AssistanceSelectableImovelQueryRepositoryImpl(imovelDatabaseDataSource = get())
    }
}
