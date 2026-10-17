package com.fstengineering.leasilycrm.core.infra.utils

import android.security.keystore.KeyPermanentlyInvalidatedException
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import java.security.GeneralSecurityException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

inline fun <T> onDatabase(block: () -> T): AppResult<T, ApplicationError> {
    try {
        return AppResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (e: Exception) {
        if (e.isFatal()) throw e
        ExceptionLogger.log(e)
        return AppResult.Error(ApplicationError.Unknown)
    }
}

fun <T> Flow<T>.asDatabaseFlowResult(): Flow<AppResult<T, ApplicationError>> {
    return distinctUntilChanged()
        .map<T, AppResult<T, ApplicationError>> { value -> AppResult.Success(value) }
        .catch { cause ->
            if (cause !is Exception || cause is CancellationException || cause.isFatal()) throw cause
            ExceptionLogger.log(cause)

            emit(AppResult.Error(ApplicationError.Unknown))
        }
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
