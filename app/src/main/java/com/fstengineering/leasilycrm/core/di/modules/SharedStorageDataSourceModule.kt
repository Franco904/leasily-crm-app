package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.SharedStorageDataSource
import org.koin.dsl.module

val sharedStorageDataSourceModule = module {
    single { SharedStorageDataSource(appContext = get()) }
}
