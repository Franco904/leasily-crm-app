package com.fstengineering.leasilycrm.core.infra.adapters

import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ExceptionLoggerBackend
import org.koin.core.context.GlobalContext

/**
 * Ponto único de registro de falhas. O backend vem do Koin, registrado em `exceptionLoggerModule`;
 * sem Koin (testes JVM) o registro é descartado. Único acesso ao Koin fora de `di/`: `object`
 * estático não é instanciado pelo Android nem recebe construtor.
 */
object ExceptionLogger {
    /**
     * Registra uma falha que não é uma exceção capturada, como um dado ausente que o código
     * mascara. A [message] vai com a stacktrace de quem chamou: a exceção é só construída, nunca
     * lançada.
     */
    fun logMasked(message: String) = log(MaskedFailure(message))

    fun log(throwable: Throwable) {
        val backend = GlobalContext.getOrNull()?.getOrNull<ExceptionLoggerBackend>()
            ?: NoOpExceptionLoggerBackend
        backend.log(throwable)
    }

    fun setUserIdentifier(identifier: String) {
        // TODO: FirebaseCrashlytics.getInstance().setUserId(identifier)
    }

    private class MaskedFailure(message: String) : Exception(message)
}
