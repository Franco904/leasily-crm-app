package com.fstengineering.leasilycrm.propertyOwners.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.presentation.models.PropertyOwnerUiState
import com.fstengineering.leasilycrm.propertyOwners.application.repositories.PropertyOwnerQueryRepository
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
class PropertyOwnersViewModelTest {
    private val propertyOwnerQueryRepositoryMock: PropertyOwnerQueryRepository = mockk(relaxed = true)
    private val propertyOwnerSharedCommandRepositoryMock: PropertyOwnerSharedCommandRepository =
        mockk(relaxed = true)

    private val sut = PropertyOwnersViewModel(
        propertyOwnerQueryRepository = propertyOwnerQueryRepositoryMock,
        propertyOwnerSharedCommandRepository = propertyOwnerSharedCommandRepositoryMock,
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("deletePropertyOwner")
    inner class DeletePropertyOwnerTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Proprietário selecionado é excluído`() =
            runTest {
                val proprietario = createPropertyOwnerUiState()
                sut.onDeletePropertyOwner(proprietario)
                coEvery { propertyOwnerSharedCommandRepositoryMock.delete(any()) } returns
                    AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.deletePropertyOwner()

                    awaitItem() shouldBeEqualTo PropertyOwnersViewModel.UiEvent.PropertyOwnerDeleteSuccess
                }
                sut.propertyOwnerToDelete.value.shouldBeNull()
                coVerify { propertyOwnerSharedCommandRepositoryMock.delete(proprietario.id) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Proprietário selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val proprietario = createPropertyOwnerUiState()
                sut.onDeletePropertyOwner(proprietario)
                coEvery { propertyOwnerSharedCommandRepositoryMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deletePropertyOwner()

                    awaitItem() shouldBeEqualTo
                        PropertyOwnersViewModel.UiEvent.BottomSheetError(ApplicationError.Unknown)
                }
                sut.propertyOwnerToDelete.value shouldBeEqualTo proprietario
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que nenhum Proprietário está selecionado`() =
            runTest {
                sut.onDeletePropertyOwner(null)

                sut.deletePropertyOwner()

                coVerify(exactly = 0) { propertyOwnerSharedCommandRepositoryMock.delete(any()) }
            }
    }

    private fun createPropertyOwnerUiState() = PropertyOwnerUiState(
        id = faker.random.nextInt(bound = 9999).toLong(),
        name = faker.random.nextUUID(),
    )
}
