package com.fstengineering.leasilycrm.core.application.di.modules

import com.fstengineering.leasilycrm.core.data.dataSources.cache.CacheLocalDataSourceImpl
import com.fstengineering.leasilycrm.core.data.dataSources.cache.interfaces.CacheLocalDataSource
import org.koin.dsl.module

val cacheModule = module {
    single<CacheLocalDataSource> { CacheLocalDataSourceImpl() }
}
