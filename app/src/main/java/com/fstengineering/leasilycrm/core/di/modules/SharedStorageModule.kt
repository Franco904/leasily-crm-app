package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.sharedStorage.SharedStorage
import org.koin.dsl.module

val sharedStorageModule = module {
    single { SharedStorage(appContext = get()) }
}
