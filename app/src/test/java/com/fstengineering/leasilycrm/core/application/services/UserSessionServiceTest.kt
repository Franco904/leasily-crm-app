package com.fstengineering.leasilycrm.core.application.services

import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.application.services.UserSessionService.UserSessionEvent
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserSessionServiceTest {
    private val userSessionSharedStoreMock: UserSessionSharedStore = mockk(relaxed = true)
    private val userRemoteDataSourceMock: UserRemoteDataSource = mockk(relaxed = true)

    private val sut = UserSessionService(
        userSessionSharedStore = userSessionSharedStoreMock,
        userRemoteDataSource = userRemoteDataSourceMock,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("onSessionExpired")
    inner class OnSessionExpiredTests {
        @Test
        fun `Deve encerrar a sessão no dispositivo e levar o Usuário ao login quando a sessão expira`() =
            runTest {
                sut.userSessionEvents.test {
                    sut.onSessionExpired()

                    awaitItem() shouldBeEqualTo UserSessionEvent.SessionExpiredEvent
                }
                coVerify { userSessionSharedStoreMock.clearSessionState() }
            }

        @Test
        fun `Deve encerrar a sessão sem acionar o back-end quando a sessão expira`() = runTest {
            sut.onSessionExpired()

            coVerify(exactly = 0) { userRemoteDataSourceMock.signOutUser() }
        }
    }

    @Nested
    @DisplayName("onUserInitiatedSignOut")
    inner class OnUserInitiatedSignOutTests {
        @Test
        fun `Deve encerrar a sessão no back-end e no dispositivo quando o Usuário sai do app`() =
            runTest {
                coEvery { userRemoteDataSourceMock.signOutUser() } returns AppResult.Success(Unit)

                sut.userSessionEvents.test {
                    sut.onUserInitiatedSignOut()

                    awaitItem() shouldBeEqualTo UserSessionEvent.UserInitiatedSignOutEvent
                }
                coVerify { userRemoteDataSourceMock.signOutUser() }
                coVerify { userSessionSharedStoreMock.clearSessionState() }
            }

        @Test
        fun `Deve encerrar a sessão no dispositivo quando o Usuário sai do app, dado que o back-end está inacessível`() =
            runTest {
                coEvery { userRemoteDataSourceMock.signOutUser() } returns
                    AppResult.Error(ApplicationError.NoInternet)

                sut.userSessionEvents.test {
                    sut.onUserInitiatedSignOut()

                    awaitItem() shouldBeEqualTo UserSessionEvent.UserInitiatedSignOutEvent
                }
                coVerify { userSessionSharedStoreMock.clearSessionState() }
            }
    }
}
