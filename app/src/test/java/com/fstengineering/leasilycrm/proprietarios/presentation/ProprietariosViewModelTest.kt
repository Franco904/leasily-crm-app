package com.fstengineering.leasilycrm.proprietarios.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarios.infra.dataSources.ProprietariosReadDataSource
import com.fstengineering.leasilycrm.proprietarios.presentation.models.ProprietariosUiState
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
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ProprietariosViewModelTest {
    private val proprietariosReadDataSourceMock: ProprietariosReadDataSource = mockk(relaxed = true)
    private val proprietarioWriteDataSourceMock: ProprietarioWriteDataSource =
        mockk(relaxed = true)

    private val sut = ProprietariosViewModel(
        proprietariosReadDataSource = proprietariosReadDataSourceMock,
        proprietarioWriteDataSource = proprietarioWriteDataSourceMock,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Proprietários quando a listagem é aberta`() = runTest {
            val proprietario1 = createProprietarioReadModel()
            val proprietario2 = createProprietarioReadModel()
            every { proprietariosReadDataSourceMock.watchProprietarios() } returns
                flowOf(AppResult.Success(listOf(proprietario1, proprietario2)))

            sut.uiState.test {
                awaitItem() shouldBeEqualTo ProprietariosUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ProprietariosUiState.Loaded(
                    proprietarios = persistentListOf(
                        ProprietarioUiState(id = proprietario1.id, name = proprietario1.name),
                        ProprietarioUiState(id = proprietario2.id, name = proprietario2.name),
                    ),
                )
            }
        }
    }

    @Nested
    @DisplayName("deleteProprietario")
    inner class DeleteProprietarioTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Proprietário selecionado é excluído`() =
            runTest {
                val proprietario = createProprietarioUiState()
                sut.onDeleteProprietario(proprietario)
                coEvery { proprietarioWriteDataSourceMock.delete(any()) } returns
                    AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.deleteProprietario()

                    awaitItem() shouldBeEqualTo ProprietariosViewModel.UiEvent.ShowDeleteSuccessSnackbar
                }
                sut.proprietarioToDelete.value.shouldBeNull()
                coVerify { proprietarioWriteDataSourceMock.delete(proprietario.id) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Proprietário selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val proprietario = createProprietarioUiState()
                sut.onDeleteProprietario(proprietario)
                coEvery { proprietarioWriteDataSourceMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deleteProprietario()

                    awaitItem() shouldBeEqualTo
                        ProprietariosViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
                sut.proprietarioToDelete.value shouldBeEqualTo proprietario
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que nenhum Proprietário está selecionado`() =
            runTest {
                sut.onDeleteProprietario(null)

                sut.deleteProprietario()

                coVerify(exactly = 0) { proprietarioWriteDataSourceMock.delete(any()) }
            }
    }

    private fun createProprietarioUiState() = ProprietarioUiState(
        id = faker.random.nextInt(bound = 9999).toLong(),
        name = faker.random.nextUUID(),
    )

    private fun createProprietarioReadModel() = ProprietarioReadModel(
        id = faker.random.nextInt(bound = 9999).toLong(),
        name = faker.random.nextUUID(),
    )
}
