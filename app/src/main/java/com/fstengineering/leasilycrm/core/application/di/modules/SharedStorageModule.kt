package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.SharedStorageImpl
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import org.koin.dsl.module

val sharedStorageModule = module {
    single<SharedStorage> { SharedStorageImpl(appContext = get()) }
}
