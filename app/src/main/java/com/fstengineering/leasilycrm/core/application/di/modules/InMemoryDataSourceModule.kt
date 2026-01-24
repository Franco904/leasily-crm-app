package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.InMemoryDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.TenantInMemoryDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces.InMemoryDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces.TenantInMemoryDataSource
import org.koin.dsl.module

val inMemoryDataSourceModule = module {
    single<InMemoryDataSource> { InMemoryDataSourceImpl() }

    single<TenantInMemoryDataSource> { TenantInMemoryDataSourceImpl(inMemoryDataSource = get()) }
}
