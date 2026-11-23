package com.fstengineering.leasilycrm.core.infra.adapters

import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLoggerBackend

/**
 * Backend de [ExceptionLogger] quando o Koin não subiu, como nos testes JVM, que não têm
 * `android.util.Log` nem Firebase.
 */
object NoOpExceptionLoggerBackend : ExceptionLoggerBackend {
    override fun log(throwable: Throwable) = Unit
}
