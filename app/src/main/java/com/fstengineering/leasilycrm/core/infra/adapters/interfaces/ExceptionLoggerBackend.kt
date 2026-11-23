package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

interface ExceptionLoggerBackend {
    fun log(throwable: Throwable)
}
