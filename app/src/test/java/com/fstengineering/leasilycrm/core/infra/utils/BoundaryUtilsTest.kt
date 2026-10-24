package com.fstengineering.leasilycrm.core.infra.utils

import android.security.keystore.KeyPermanentlyInvalidatedException
import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import java.io.IOException
import java.security.GeneralSecurityException
import javax.crypto.AEADBadTagException
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoundaryUtilsTest {
    @BeforeEach
    fun setUp() {
        mockkObject(ExceptionLogger)
        every { ExceptionLogger.log(any()) } just runs
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(ExceptionLogger)
    }

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
            mockk<KeyPermanentlyInvalidatedException>().isFatal().shouldBeFalse()
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
        fun `Deve retornar erro desconhecido e registrar a falha quando o bloco lança exceção comum`() {
            val exception = IOException()

            val result = onDatabase { throw exception }

            result shouldBeEqualTo AppResult.Error(ApplicationError.Unknown)
            verify(exactly = 1) { ExceptionLogger.log(exception) }
        }

        @Test
        fun `Deve propagar a exceção sem registrar quando ela é fatal`() {
            val exception = GeneralSecurityException()

            val thrown = assertThrows<GeneralSecurityException> { onDatabase { throw exception } }

            thrown shouldBe exception
            verify(exactly = 0) { ExceptionLogger.log(any()) }
        }

        @Test
        fun `Deve propagar o cancelamento quando a coroutine é cancelada`() {
            assertThrows<CancellationException> { onDatabase { throw CancellationException() } }
        }
    }

    @Nested
    @DisplayName("asDatabaseFlowResult")
    inner class AsDatabaseFlowResultTests {
        @Test
        fun `Deve emitir erro desconhecido e registrar a falha quando o flow lança exceção comum`() =
            runTest {
                val exception = IOException()

                flow<Int> { throw exception }.asDatabaseFlowResult().test {
                    awaitItem() shouldBeEqualTo AppResult.Error(ApplicationError.Unknown)
                    awaitComplete()
                }
                verify(exactly = 1) { ExceptionLogger.log(exception) }
            }

        @Test
        fun `Deve propagar a exceção sem registrar quando ela é fatal`() = runTest {
            val exception = SecurityException()

            flow<Int> { throw exception }.asDatabaseFlowResult().test {
                awaitError() shouldBe exception
            }
            verify(exactly = 0) { ExceptionLogger.log(any()) }
        }
    }
}
