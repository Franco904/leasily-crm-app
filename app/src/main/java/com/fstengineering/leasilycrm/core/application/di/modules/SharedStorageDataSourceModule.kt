package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.SharedStorageDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import org.koin.dsl.module

val sharedStorageDataSourceModule = module {
    single<SharedStorageDataSource> { SharedStorageDataSourceImpl(appContext = get()) }
}
