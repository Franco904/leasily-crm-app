package com.fstengineering.leasilycrm.core.infra.dataSources.remote

import com.fstengineering.leasilycrm.core.infra.constants.SIGN_IN_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.SIGN_OUT_ROUTE
import com.fstengineering.leasilycrm.core.infra.constants.UPDATE_USER_ROUTE
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SessionValidationTest {
    private val onSessionExpiredMock: suspend () -> Unit = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("validateSession")
    inner class ValidateSessionTests {
        @Test
        fun `Deve encerrar a sessão quando o back-end recusa o token de sessão por não autenticado`() =
            runTest {
                request(route = UPDATE_USER_ROUTE, status = HttpStatusCode.Unauthorized)

                coVerify(exactly = 1) { onSessionExpiredMock() }
            }

        @Test
        fun `Deve encerrar a sessão quando o back-end recusa o token de sessão por acesso negado`() =
            runTest {
                request(route = UPDATE_USER_ROUTE, status = HttpStatusCode.Forbidden)

                coVerify(exactly = 1) { onSessionExpiredMock() }
            }

        @Test
        fun `Deve manter a sessão quando o back-end atende a requisição`() = runTest {
            request(route = UPDATE_USER_ROUTE, status = HttpStatusCode.OK)

            coVerify(exactly = 0) { onSessionExpiredMock() }
        }

        @Test
        fun `Deve manter a sessão quando o back-end falha por motivo alheio à sessão`() = runTest {
            request(route = UPDATE_USER_ROUTE, status = HttpStatusCode.InternalServerError)

            coVerify(exactly = 0) { onSessionExpiredMock() }
        }

        @Test
        fun `Deve manter o Usuário no login quando o back-end recusa o login`() = runTest {
            request(route = SIGN_IN_ROUTE, status = HttpStatusCode.Unauthorized)

            coVerify(exactly = 0) { onSessionExpiredMock() }
        }

        @Test
        fun `Deve não sinalizar sessão expirada quando o Usuário sai do app, dado que o back-end já recusa o token de sessão`() =
            runTest {
                request(route = SIGN_OUT_ROUTE, status = HttpStatusCode.Unauthorized)

                coVerify(exactly = 0) { onSessionExpiredMock() }
            }
    }

    private suspend fun request(route: String, status: HttpStatusCode) {
        val client = HttpClient(MockEngine { respond(content = "", status = status) }) {
            validateSession(onSessionExpired = onSessionExpiredMock)
        }

        client.get("https://leasily.test$route")
    }
}
