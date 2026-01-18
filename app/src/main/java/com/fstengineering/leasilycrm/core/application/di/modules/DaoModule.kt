package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.data.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSearchedNeighborhoodDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CrmMetricsDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertySeaProximityTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantContactOriginTypeDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import org.koin.dsl.module

val daoModule = module {
    single<AppDatabase> { AppDatabase.buildDatabase(appContext = get()) }

    single<AssistanceDao> { get<AppDatabase>().assistanceDao() }
    single<AssistanceCharacteristicAssistanceCrossRefDao> {
        get<AppDatabase>().assistanceCharacteristicAssistanceCrossRefDao()
    }
    single<AssistanceCharacteristicDao> { get<AppDatabase>().assistanceCharacteristicDao() }
    single<AssistanceInteractionDao> { get<AppDatabase>().assistanceInteractionDao() }
    single<AssistanceSearchedNeighborhoodDao> {
        get<AppDatabase>().assistanceSearchedNeighborhoodDao()
    }
    single<AssistanceSentPropertyCrossRefDao> { get<AppDatabase>().assistanceSentPropertyCrossRefDao() }
    single<CrmMetricsDao> { get<AppDatabase>().crmMetricsDao() }
    single<CustomerSegmentDao> { get<AppDatabase>().customerSegmentDao() }
    single<PropertyDao> { get<AppDatabase>().propertyDao() }
    single<PropertyCharacteristicPropertyCrossRefDao> {
        get<AppDatabase>().propertyCharacteristicPropertyCrossRefDao()
    }
    single<PropertyCharacteristicDao> { get<AppDatabase>().propertyCharacteristicDao() }
    single<PropertyFileDao> { get<AppDatabase>().propertyFileDao() }
    single<PropertyOwnerDao> { get<AppDatabase>().propertyOwnerDao() }
    single<PropertyOwnerFileDao> { get<AppDatabase>().propertyOwnerFileDao() }
    single<PropertyPriceDao> { get<AppDatabase>().propertyPriceDao() }
    single<PropertySeaProximityTypeDao> { get<AppDatabase>().propertySeaProximityTypeDao() }
    single<PropertyTypeDao> { get<AppDatabase>().propertyTypeDao() }
    single<StayDao> { get<AppDatabase>().stayDao() }
    single<StayFileDao> { get<AppDatabase>().stayFileDao() }
    single<StayTransferDao> { get<AppDatabase>().stayTransferDao() }
    single<TenantDao> { get<AppDatabase>().tenantDao() }
    single<TenantContactOriginTypeDao> { get<AppDatabase>().tenantContactOriginTypeDao() }
}
