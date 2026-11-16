package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.infra.adapters.DatabaseAdapterImpl
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceInteractionDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceSearchedNeighborhoodDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceSentImovelCrossRefDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.UserAnalyticsDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayFileDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayTransferDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentImovelCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.UserAnalyticsDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayTransferDatabaseDataSource
import org.koin.dsl.module

val databaseDataSourceModule = module {
    single { get<AppDatabase>().imovelSharedWriteDataSource() }
    single { get<AppDatabase>().clienteSharedReadDataSource() }

    single<DatabaseAdapter> { DatabaseAdapterImpl(db = get()) }

    single<AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource> {
        AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl(dao = get())
    }

    single<AssistanceDatabaseDataSource> {
        AssistanceDatabaseDataSourceImpl(dao = get())
    }

    single<AssistanceInteractionDatabaseDataSource> {
        AssistanceInteractionDatabaseDataSourceImpl(dao = get())
    }

    single<AssistanceSearchedNeighborhoodDatabaseDataSource> {
        AssistanceSearchedNeighborhoodDatabaseDataSourceImpl(dao = get())
    }

    single<AssistanceSentImovelCrossRefDatabaseDataSource> {
        AssistanceSentImovelCrossRefDatabaseDataSourceImpl(dao = get())
    }

    single<UserAnalyticsDatabaseDataSource> {
        UserAnalyticsDatabaseDataSourceImpl(dao = get())
    }


    single<ImovelDatabaseDataSource> {
        ImovelDatabaseDataSourceImpl(dao = get())
    }



    single<StayDatabaseDataSource> {
        StayDatabaseDataSourceImpl(dao = get(), dateTimeProvider = get<AppDateTimeProvider>())
    }

    single<StayFileDatabaseDataSource> {
        StayFileDatabaseDataSourceImpl(dao = get())
    }

    single<StayTransferDatabaseDataSource> {
        StayTransferDatabaseDataSourceImpl(dao = get())
    }
}

