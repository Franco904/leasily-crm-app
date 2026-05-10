package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.SharedStorageDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import org.koin.dsl.module

val sharedStorageDataSourceModule = module {
    single<SharedStorageDataSource> { SharedStorageDataSourceImpl(appContext = get()) }
}
