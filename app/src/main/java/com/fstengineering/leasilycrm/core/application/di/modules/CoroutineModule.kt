package com.fstengineering.leasilycrm.core.application.di.modules

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val coroutineModule = module {
    // application scope
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
}
