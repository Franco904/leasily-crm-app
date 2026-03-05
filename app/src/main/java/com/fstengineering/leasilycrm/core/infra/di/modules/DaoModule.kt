package com.fstengineering.leasilycrm.core.infra.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistancePreferredNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.CrmMetricsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.TenantDao
import org.koin.dsl.module

val daoModule = module {
    single<AppDatabase> { AppDatabase.buildDatabase(appContext = get()) }

    single<AssistanceDao> { get<AppDatabase>().assistanceDao() }
    single<AssistanceCharacteristicAssistanceCrossRefDao> {
        get<AppDatabase>().assistanceCharacteristicAssistanceCrossRefDao()
    }
    single<AssistanceInteractionDao> { get<AppDatabase>().assistanceInteractionDao() }
    single<AssistancePreferredNeighborhoodDao> {
        get<AppDatabase>().assistanceSearchedNeighborhoodDao()
    }
    single<AssistanceSentPropertyCrossRefDao> { get<AppDatabase>().assistanceSentPropertyCrossRefDao() }
    single<CrmMetricsDao> { get<AppDatabase>().crmMetricsDao() }
    single<PropertyDao> { get<AppDatabase>().propertyDao() }
    single<PropertyCharacteristicPropertyCrossRefDao> {
        get<AppDatabase>().propertyCharacteristicPropertyCrossRefDao()
    }
    single<PropertyFileDao> { get<AppDatabase>().propertyFileDao() }
    single<PropertyOwnerDao> { get<AppDatabase>().propertyOwnerDao() }
    single<PropertyOwnerFileDao> { get<AppDatabase>().propertyOwnerFileDao() }
    single<PropertyPriceDao> { get<AppDatabase>().propertyPriceDao() }
    single<StayDao> { get<AppDatabase>().stayDao() }
    single<StayFileDao> { get<AppDatabase>().stayFileDao() }
    single<StayTransferDao> { get<AppDatabase>().stayTransferDao() }
    single<TenantDao> { get<AppDatabase>().tenantDao() }
}
