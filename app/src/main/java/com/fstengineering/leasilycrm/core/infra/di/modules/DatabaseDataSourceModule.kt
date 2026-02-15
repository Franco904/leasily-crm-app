package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.DatabaseAdapterImpl
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceCharacteristicDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceInteractionDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceSearchedNeighborhoodDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AssistanceSentPropertyCrossRefDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.CrmMetricsDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyCharacteristicDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyCharacteristicPropertyCrossRefDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyFileDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyOwnerDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyOwnerFileDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyPriceDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertySeaProximityTypeDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.PropertyTypeDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayFileDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.StayTransferDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.TenantContactOriginTypeDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.TenantDatabaseDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSearchedNeighborhoodDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.CrmMetricsDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyPriceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertySeaProximityTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayTransferDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantContactOriginTypeDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import org.koin.dsl.module

val databaseDataSourceModule = module {
    single<DatabaseAdapter> { DatabaseAdapterImpl(db = get()) }

    single<AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource> {
        AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl(dao = get())
    }

    single<AssistanceCharacteristicDatabaseDataSource> {
        AssistanceCharacteristicDatabaseDataSourceImpl(dao = get())
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

    single<AssistanceSentPropertyCrossRefDatabaseDataSource> {
        AssistanceSentPropertyCrossRefDatabaseDataSourceImpl(dao = get())
    }

    single<CrmMetricsDatabaseDataSource> {
        CrmMetricsDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyCharacteristicDatabaseDataSource> {
        PropertyCharacteristicDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyCharacteristicPropertyCrossRefDatabaseDataSource> {
        PropertyCharacteristicPropertyCrossRefDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyDatabaseDataSource> {
        PropertyDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyFileDatabaseDataSource> {
        PropertyFileDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyOwnerDatabaseDataSource> {
        PropertyOwnerDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyOwnerFileDatabaseDataSource> {
        PropertyOwnerFileDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyPriceDatabaseDataSource> {
        PropertyPriceDatabaseDataSourceImpl(dao = get())
    }

    single<PropertySeaProximityTypeDatabaseDataSource> {
        PropertySeaProximityTypeDatabaseDataSourceImpl(dao = get())
    }

    single<PropertyTypeDatabaseDataSource> {
        PropertyTypeDatabaseDataSourceImpl(dao = get())
    }

    single<StayDatabaseDataSource> {
        StayDatabaseDataSourceImpl(dao = get())
    }

    single<StayFileDatabaseDataSource> {
        StayFileDatabaseDataSourceImpl(dao = get())
    }

    single<StayTransferDatabaseDataSource> {
        StayTransferDatabaseDataSourceImpl(dao = get())
    }

    single<TenantContactOriginTypeDatabaseDataSource> {
        TenantContactOriginTypeDatabaseDataSourceImpl(dao = get())
    }

    single<TenantDatabaseDataSource> {
        TenantDatabaseDataSourceImpl(dao = get())
    }
}

