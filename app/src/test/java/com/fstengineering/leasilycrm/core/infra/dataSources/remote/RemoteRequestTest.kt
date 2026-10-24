package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.infra.adapters.ExceptionLogger
import com.fstengineering.leasilycrm.testUtils.faker
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.HttpMethod
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RemoteRequestTest {
    private val connectivityAdapterMock: ConnectivityAdapter = mockk()

    private val sut = HttpClient(MockEngine { throw IOException("Falha de rede") }) {
        classifyNetworkFailures(connectivityAdapter = connectivityAdapterMock)
    }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        mockkObject(ExceptionLogger)
        every { ExceptionLogger.log(any()) } just runs
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(ExceptionLogger)
    }

    @Nested
    @DisplayName("requestBackend")
    inner class RequestBackendTests {
        @Test
        fun `Deve retornar erro de sem internet quando a requisição falha por rede, dado que o aparelho está sem conexão`() =
            runTest {
                every { connectivityAdapterMock.isDeviceConnectedToInternet() } returns false

                val result = sut.requestBackend(url = randomUrl(), method = HttpMethod.Get)

                result.shouldBeInstanceOf<AppResult.Error<*, *>>()
                (result as AppResult.Error).error shouldBeEqualTo ApplicationError.NoInternet
            }
    }

    private fun randomUrl() = "https://leasily.test/${faker.random.nextUUID()}"
}
