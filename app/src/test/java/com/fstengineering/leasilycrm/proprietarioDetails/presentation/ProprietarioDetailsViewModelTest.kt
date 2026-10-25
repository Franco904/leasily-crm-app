package com.fstengineering.leasilycrm.proprietarioDetails.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources.ProprietarioDetailsReadDataSource
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioFileUiState
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ProprietarioDetailsViewModelTest {
    private val proprietarioDetailsReadDataSourceMock: ProprietarioDetailsReadDataSource =
        mockk(relaxed = true)
    private val proprietarioWriteDataSourceMock: ProprietarioWriteDataSource =
        mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Proprietário quando a tela de detalhes é aberta`() = runTest {
            val proprietarioId = faker.random.nextLong(bound = 9999)
            val proprietario = createProprietarioReadModel()
            every { proprietarioDetailsReadDataSourceMock.watchProprietario(proprietarioId) } returns
                flowOf(AppResult.Success(proprietario))
            val sut = createSut(proprietarioId = proprietarioId)

            sut.uiState.test {
                awaitItem() shouldBeEqualTo ProprietarioUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ProprietarioUiState.Loaded(
                    name = proprietario.name,
                    phone = proprietario.phone,
                    email = proprietario.email,
                    paymentKey = proprietario.paymentKey,
                    files = persistentListOf(
                        ProprietarioFileUiState(
                            uriString = proprietario.files.single().uriString,
                            name = proprietario.files.single().name,
                        ),
                    ),
                )
            }
        }
    }

    @Nested
    @DisplayName("deleteProprietario")
    inner class DeleteProprietarioTests {
        @Test
        fun `Deve confirmar exclusão quando Proprietário é excluído`() = runTest {
            val proprietarioId = faker.random.nextLong(bound = 9999)
            coEvery { proprietarioWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)
            val sut = createSut(proprietarioId = proprietarioId)

            sut.uiEvents.test {
                sut.deleteProprietario()

                awaitItem() shouldBeEqualTo
                    ProprietarioDetailsViewModel.UiEvent.ShowDeleteSuccessSnackbar
            }
            coVerify { proprietarioWriteDataSourceMock.delete(proprietarioId) }
        }

        @Test
        fun `Deve exibir falha quando Proprietário é excluído, dado que o armazenamento local falha`() =
            runTest {
                val proprietarioId = faker.random.nextLong(bound = 9999)
                coEvery { proprietarioWriteDataSourceMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)
                val sut = createSut(proprietarioId = proprietarioId)

                sut.uiEvents.test {
                    sut.deleteProprietario()

                    awaitItem() shouldBeEqualTo
                        ProprietarioDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }
    }

    private fun createSut(proprietarioId: Long) = ProprietarioDetailsViewModel(
        route = Destinations.ProprietarioDetails(proprietarioId),
        proprietarioDetailsReadDataSource = proprietarioDetailsReadDataSourceMock,
        proprietarioWriteDataSource = proprietarioWriteDataSourceMock,
    )

    private fun createProprietarioReadModel() = ProprietarioReadModel(
        name = faker.random.nextUUID(),
        phone = faker.random.nextUUID(),
        email = faker.random.nextUUID(),
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf(
            ProprietarioFileReadModel(
                uriString = faker.random.nextUUID(),
                name = faker.random.nextUUID(),
            ),
        ),
    )
}
