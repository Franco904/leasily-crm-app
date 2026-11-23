package com.fstengineering.leasilycrm.core.infra.utils

import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.datastore.core.CorruptionException
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.security.GeneralSecurityException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Executa [block] na borda de infra e agrega a falha em [AppResult.Error] com o erro de [toError]
 * (padrão: [ApplicationError.Unknown]).
 *
 * Único lugar do app que captura `Exception` genérica (`docs/guidelines/0011-error-handling.md`,
 * passo 3): relança [CancellationException] e exceção fatal ([isFatal]), e registra no
 * [ExceptionLogger] a que converte em erro.
 */
inline fun <T> onBoundary(
    toError: (Exception) -> BaseError = { ApplicationError.Unknown },
    block: () -> T,
): AppResult<T> {
    try {
        return AppResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        if (e.isFatal()) throw e
        ExceptionLogger.log(e)
        return AppResult.Error(toError(e))
    }
}

/** [onBoundary] para o banco de dados: falha vira [ApplicationError.Unknown]. */
inline fun <T> onDatabase(block: () -> T): AppResult<T> = onBoundary(block = block)

/**
 * Executa [block] e, se ele falha com [Exception] recuperável, registra no [ExceptionLogger] e
 * devolve [fallback] — a falha fica invisível para o chamador, então a função que usa este wrapper
 * declara a degradação no próprio KDoc. [CancellationException] e exceção fatal ([isFatal]) são
 * relançadas.
 */
inline fun <T> maskFailure(fallback: T, block: () -> T): T {
    try {
        return block()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        if (e.isFatal()) throw e
        ExceptionLogger.log(e)
        return fallback
    }
}

/**
 * Executa [block] de leitura de um `Serializer` do DataStore e converte a falha recuperável em
 * [CorruptionException], contrato do DataStore para arquivo ilegível; o `corruptionHandler` do
 * DataStore decide o valor de substituição. [CancellationException] e exceção fatal ([isFatal])
 * são relançadas.
 */
inline fun <T> onSerializationRead(block: () -> T): T {
    try {
        return block()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        if (e.isFatal()) throw e
        ExceptionLogger.log(e)
        throw CorruptionException("Arquivo de preferências ilegível", e)
    }
}

fun <T> Flow<T>.asDatabaseFlowResult(): Flow<AppResult<T>> {
    return distinctUntilChanged()
        .map<T, AppResult<T>> { value -> AppResult.Success(value) }
        .catchException { emit(AppResult.Error(ApplicationError.Unknown)) }
}

/**
 * Como [catch], mas entrega a [action] **apenas** [Exception] recuperável, já registrada no
 * [ExceptionLogger].
 *
 * [Error] da plataforma (OOM, `StackOverflowError`, …), [CancellationException] (cooperação com o
 * cancelamento estruturado de coroutine) e exceções fatais ([isFatal]) são relançados e nunca
 * chegam em [action].
 */
fun <T> Flow<T>.catchException(
    action: suspend FlowCollector<T>.(exception: Exception) -> Unit,
): Flow<T> = catch { cause ->
    if (cause !is Exception || cause is CancellationException || cause.isFatal()) throw cause
    ExceptionLogger.log(cause)

    action(cause)
}

/**
 * Exceção que sinaliza estado irrecuperável do ambiente — permissão negada pelo SO ou dado
 * criptografado não confiável — e por isso nunca é convertida em erro na borda
 * (`docs/guidelines/0011-error-handling.md`, passo 4). Invalidação de chave do Keystore prevista
 * pela plataforma não é fatal: é evento esperado, tratado em [CryptoUtils].
 */
fun Exception.isFatal(): Boolean =
    this is SecurityException ||
            (this is GeneralSecurityException && this !is KeyPermanentlyInvalidatedException)
