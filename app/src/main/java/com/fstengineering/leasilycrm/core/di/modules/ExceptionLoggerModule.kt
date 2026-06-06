package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.CrashlyticsExceptionLoggerAdapter
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLogger
import org.koin.dsl.module

val exceptionLoggerModule = module {
    single<ExceptionLogger> { CrashlyticsExceptionLoggerAdapter() }
}
