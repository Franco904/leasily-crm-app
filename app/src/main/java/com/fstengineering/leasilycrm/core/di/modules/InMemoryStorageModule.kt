package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.inMemory.InMemoryStorageImpl
import com.fstengineering.leasilycrm.core.infra.inMemory.interfaces.InMemoryStorage
import org.koin.dsl.module

val inMemoryStorageModule = module {
    single<InMemoryStorage> { InMemoryStorageImpl() }
}
