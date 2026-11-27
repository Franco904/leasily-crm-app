package com.fstengineering.leasilycrm.core.infra.utils

import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.datastore.core.CorruptionException
import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.invoking
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.IOException
import java.security.GeneralSecurityException
import javax.crypto.AEADBadTagException
import kotlin.coroutines.cancellation.CancellationException

class BoundaryUtilsTest {
    @Nested
    @DisplayName("isFatal")
    inner class IsFatalTests {
        @Test
        fun `Deve considerar fatal quando o SO nega a permissão`() {
            SecurityException().isFatal().shouldBeTrue()
        }

        @Test
        fun `Deve considerar fatal quando o dado criptografado falha na autenticação`() {
            AEADBadTagException().isFatal().shouldBeTrue()
        }

        @Test
        fun `Deve considerar não fatal quando a plataforma invalida a chave do Keystore`() {
            mockk<KeyPermanentlyInvalidatedException>(relaxUnitFun = true).isFatal().shouldBeFalse()
        }

        @Test
        fun `Deve considerar não fatal quando a falha é de leitura de arquivo`() {
            IOException().isFatal().shouldBeFalse()
        }
    }

    @Nested
    @DisplayName("onDatabase")
    inner class OnDatabaseTests {
        @Test
        fun `Deve retornar o valor quando o bloco conclui`() {
            val result = onDatabase { 42 }

            result shouldBeEqualTo AppResult.Success(42)
        }

        @Test
        fun `Deve retornar erro desconhecido quando o bloco lança exceção comum`() {
            val exception = IOException()

            val result = onDatabase { throw exception }

            result shouldBeEqualTo AppResult.Error(ApplicationError.Unknown)
        }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() {
            val exception = GeneralSecurityException()

            val thrown = invoking { onDatabase { throw exception } } shouldThrow
                GeneralSecurityException::class

            thrown.exception shouldBe exception
        }

        @Test
        fun `Deve propagar o cancelamento quando a coroutine é cancelada`() {
            invoking { onDatabase { throw CancellationException() } } shouldThrow
                CancellationException::class
        }
    }

    @Nested
    @DisplayName("asDatabaseFlowResult")
    inner class AsDatabaseFlowResultTests {
        @Test
        fun `Deve emitir erro desconhecido quando o flow lança exceção comum`() =
            runTest {
                val exception = IOException()

                flow<Int> { throw exception }.asDatabaseFlowResult().test {
                    awaitItem() shouldBeEqualTo AppResult.Error(ApplicationError.Unknown)
                    awaitComplete()
                }
            }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() = runTest {
            val exception = SecurityException()

            flow<Int> { throw exception }.asDatabaseFlowResult().test {
                awaitError() shouldBe exception
            }
        }
    }

    @Nested
    @DisplayName("onBoundary")
    inner class OnBoundaryTests {
        @Test
        fun `Deve retornar o valor quando o bloco conclui`() {
            val result = onBoundary { 42 }

            result shouldBeEqualTo AppResult.Success(42)
        }

        @Test
        fun `Deve retornar o erro escolhido quando o bloco lança exceção comum`() {
            val result =
                onBoundary(toError = { ApplicationError.NoInternet }) { throw IOException() }

            result shouldBeEqualTo AppResult.Error(ApplicationError.NoInternet)
        }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() {
            val exception = SecurityException()

            val thrown = invoking { onBoundary { throw exception } } shouldThrow
                SecurityException::class

            thrown.exception shouldBe exception
        }

        @Test
        fun `Deve propagar o cancelamento quando a coroutine é cancelada`() {
            invoking { onBoundary { throw CancellationException() } } shouldThrow
                CancellationException::class
        }
    }

    @Nested
    @DisplayName("maskFailure")
    inner class MaskFailureTests {
        @Test
        fun `Deve retornar o valor quando o bloco conclui`() {
            val result = maskFailure(fallback = 0) { 42 }

            result shouldBeEqualTo 42
        }

        @Test
        fun `Deve retornar o valor substituto quando o bloco lança exceção comum`() {
            val result = maskFailure(fallback = 0) { throw IOException() }

            result shouldBeEqualTo 0
        }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() {
            val exception = SecurityException()

            val thrown = invoking { maskFailure(fallback = 0) { throw exception } } shouldThrow
                SecurityException::class

            thrown.exception shouldBe exception
        }

        @Test
        fun `Deve propagar o cancelamento quando a coroutine é cancelada`() {
            invoking { maskFailure(fallback = 0) { throw CancellationException() } } shouldThrow
                CancellationException::class
        }
    }

    @Nested
    @DisplayName("onSerializationRead")
    inner class OnSerializationReadTests {
        @Test
        fun `Deve retornar o valor quando o bloco conclui`() {
            val result = onSerializationRead { 42 }

            result shouldBeEqualTo 42
        }

        @Test
        fun `Deve sinalizar arquivo ilegível quando o bloco lança exceção comum`() {
            val exception = IOException()

            val thrown = invoking { onSerializationRead { throw exception } } shouldThrow
                CorruptionException::class

            thrown.exception.cause shouldBe exception
        }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() {
            val exception = SecurityException()

            val thrown = invoking { onSerializationRead { throw exception } } shouldThrow
                SecurityException::class

            thrown.exception shouldBe exception
        }

        @Test
        fun `Deve propagar o cancelamento quando a coroutine é cancelada`() {
            invoking { onSerializationRead { throw CancellationException() } } shouldThrow
                CancellationException::class
        }
    }

    @Nested
    @DisplayName("catchException")
    inner class CatchExceptionTests {
        @Test
        fun `Deve manter os valores quando o flow conclui sem falha`() = runTest {
            flowOf(42).catchException { emit(0) }.test {
                awaitItem() shouldBeEqualTo 42
                awaitComplete()
            }
        }

        @Test
        fun `Deve entregar a exceção à ação quando o flow lança exceção comum`() = runTest {
            val exception = IOException()
            val received = mutableListOf<Exception>()

            flow<Int> { throw exception }
                .catchException {
                    received.add(it)
                    emit(0)
                }
                .test {
                    awaitItem() shouldBeEqualTo 0
                    awaitComplete()
                }

            received shouldBeEqualTo listOf(exception)
        }

        @Test
        fun `Deve propagar a exceção quando ela é fatal`() = runTest {
            val exception = SecurityException()

            flow<Int> { throw exception }.catchException { emit(0) }.test {
                awaitError() shouldBe exception
            }
        }

        @Test
        fun `Deve propagar o erro da plataforma quando o flow o lança`() = runTest {
            val error = StackOverflowError()

            flow<Int> { throw error }.catchException { emit(0) }.test {
                awaitError() shouldBe error
            }
        }

        @Test
        fun `Deve propagar o cancelamento quando o flow é cancelado`() = runTest {
            val cancellation = CancellationException()

            flow<Int> { throw cancellation }.catchException { emit(0) }.test {
                awaitError() shouldBeInstanceOf CancellationException::class
            }
        }
    }
}
