package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

interface ExceptionLogger {
    fun log(throwable: Throwable)
    fun setUserIdentifier(identifier: String)
}
