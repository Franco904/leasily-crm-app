package com.fstengineering.leasilycrm.proprietarios.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.application.repositories.ProprietarioSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarios.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ProprietariosViewModelTest {
    private val proprietarioQueryRepositoryMock: ProprietarioQueryRepository = mockk(relaxed = true)
    private val proprietarioSharedCommandRepositoryMock: ProprietarioSharedCommandRepository =
        mockk(relaxed = true)

    private val sut = ProprietariosViewModel(
        proprietarioQueryRepository = proprietarioQueryRepositoryMock,
        proprietarioSharedCommandRepository = proprietarioSharedCommandRepositoryMock,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("deleteProprietario")
    inner class DeleteProprietarioTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Proprietário selecionado é excluído`() =
            runTest {
                val proprietario = createProprietarioUiState()
                sut.onDeleteProprietario(proprietario)
                coEvery { proprietarioSharedCommandRepositoryMock.delete(any()) } returns
                    AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.deleteProprietario()

                    awaitItem() shouldBeEqualTo ProprietariosViewModel.UiEvent.ProprietarioDeleteSuccess
                }
                sut.proprietarioToDelete.value.shouldBeNull()
                coVerify { proprietarioSharedCommandRepositoryMock.delete(proprietario.id) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Proprietário selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val proprietario = createProprietarioUiState()
                sut.onDeleteProprietario(proprietario)
                coEvery { proprietarioSharedCommandRepositoryMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deleteProprietario()

                    awaitItem() shouldBeEqualTo
                        ProprietariosViewModel.UiEvent.BottomSheetError(ApplicationError.Unknown)
                }
                sut.proprietarioToDelete.value shouldBeEqualTo proprietario
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que nenhum Proprietário está selecionado`() =
            runTest {
                sut.onDeleteProprietario(null)

                sut.deleteProprietario()

                coVerify(exactly = 0) { proprietarioSharedCommandRepositoryMock.delete(any()) }
            }
    }

    private fun createProprietarioUiState() = ProprietarioUiState(
        id = faker.random.nextInt(bound = 9999).toLong(),
        name = faker.random.nextUUID(),
    )
}
