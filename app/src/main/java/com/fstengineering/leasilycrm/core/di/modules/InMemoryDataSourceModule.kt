package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.InMemoryDataSourceImpl
import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.InMemoryDataSource
import org.koin.dsl.module

val inMemoryDataSourceModule = module {
    single<InMemoryDataSource> { InMemoryDataSourceImpl() }
}
