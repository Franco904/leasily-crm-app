package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistancePreferredNeighborhoodDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.LeadTenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
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
    single<UserAnalyticsDao> { get<AppDatabase>().userAnalyticsDao() }
    single<PropertyDao> { get<AppDatabase>().propertyDao() }
    single<PropertyCharacteristicPropertyCrossRefDao> {
        get<AppDatabase>().propertyCharacteristicPropertyCrossRefDao()
    }
    single<PropertyFileDao> { get<AppDatabase>().propertyFileDao() }
    single<PropertyPriceDao> { get<AppDatabase>().propertyPriceDao() }
    single<StayDao> { get<AppDatabase>().stayDao() }
    single<StayFileDao> { get<AppDatabase>().stayFileDao() }
    single<StayTransferDao> { get<AppDatabase>().stayTransferDao() }
    single<LeadTenantDao> { get<AppDatabase>().leadTenantDao() }
}
