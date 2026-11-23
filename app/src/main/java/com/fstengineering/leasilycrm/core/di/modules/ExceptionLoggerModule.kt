package com.fstengineering.leasilycrm.core.di.modules

import com.fstengineering.leasilycrm.core.infra.adapters.LogcatExceptionLoggerBackend
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLoggerBackend
import org.koin.dsl.module

val exceptionLoggerModule = module {
    single<ExceptionLoggerBackend> { LogcatExceptionLoggerBackend() }
}
