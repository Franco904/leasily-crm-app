package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.database.AppDatabase
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayTransferDao
import org.koin.dsl.module

val daoModule = module {
    single<AppDatabase> { AppDatabase.buildDatabase(appContext = get()) }

    single<UserAnalyticsDao> { get<AppDatabase>().userAnalyticsDao() }
    single<ImovelDao> { get<AppDatabase>().imovelDao() }
    single<StayDao> { get<AppDatabase>().stayDao() }
    single<StayFileDao> { get<AppDatabase>().stayFileDao() }
    single<StayTransferDao> { get<AppDatabase>().stayTransferDao() }
}
