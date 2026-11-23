package com.fstengineering.leasilycrm.clientes.presentation.leads

import app.cash.turbine.test
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clientes.infra.localStorage.ClientesLocalStorage
import com.fstengineering.leasilycrm.clientes.infra.dataSources.ClientesReadDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.presentation.models.ClienteOrderType
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class LeadsViewModelTest {
    private val clientesReadDataSourceMock: ClientesReadDataSource = mockk(relaxUnitFun = true)
    private val clienteWriteDataSourceMock: ClienteWriteDataSource = mockk(relaxUnitFun = true)
    private val clientesLocalStorageMock: ClientesLocalStorage =
        mockk(relaxUnitFun = true)

    private fun createSut() = LeadsViewModel(
        clientesReadDataSource = clientesReadDataSourceMock,
        clienteWriteDataSource = clienteWriteDataSourceMock,
        clientesLocalStorage = clientesLocalStorageMock,
    )

    @BeforeEach
    fun setUp() {
        coEvery { clientesLocalStorageMock.getLeadsOrderTypeId() } returns
                AppResult.Success(ClienteOrderType.Name.id)
        every { clientesReadDataSourceMock.watchClientes(any(), any(), any()) } returns
                flowOf(AppResult.Success(emptyList()))
        coEvery { clientesLocalStorageMock.saveLeadsOrderTypeId(any()) } returns
                AppResult.Success(Unit)
        coEvery { clientesReadDataSourceMock.getLeads(any(), any()) } returns
                AppResult.Success(emptyList())
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Leads na ordem preferida quando a listagem é aberta`() = runTest {
            val sut = createSut()
            val lead = createClienteReadModel()
            coEvery { clientesLocalStorageMock.getLeadsOrderTypeId() } returns
                    AppResult.Success(ClienteOrderType.LastUpdateDate.id)
            every {
                clientesReadDataSourceMock.watchClientes(
                    status = ClienteStatus.Lead,
                    matching = null,
                    orderTypeId = ClienteOrderType.LastUpdateDate.id,
                )
            } returns flowOf(AppResult.Success(listOf(lead)))

            sut.init()

            sut.leads.value.single().cliente.id shouldBeEqualTo lead.id
            sut.orderType.value shouldBeEqualTo ClienteOrderType.LastUpdateDate
            sut.isLoading.value shouldBeEqualTo false
        }

        @Test
        fun `Deve informar falha quando a listagem é aberta, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSut()
                every { clientesReadDataSourceMock.watchClientes(any(), any(), any()) } returns
                        flowOf(AppResult.Error(ApplicationError.Unknown))

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                            LeadsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }

        @Test
        fun `Deve observar Leads uma única vez quando a tela chama init novamente`() = runTest {
            val sut = createSut()
            sut.init()
            sut.init()

            coVerify(exactly = 1) { clientesLocalStorageMock.getLeadsOrderTypeId() }
        }
    }

    @Nested
    @DisplayName("onOrderTypeChange")
    inner class OnOrderTypeChangeTests {
        @Test
        fun `Deve guardar a preferência de ordenação dos Leads quando a ordenação é trocada`() =
            runTest {
                val sut = createSut()
                sut.init()

                sut.onOrderTypeChange(ClienteOrderType.LastUpdateDate)

                coVerify { clientesLocalStorageMock.saveLeadsOrderTypeId(ClienteOrderType.LastUpdateDate.id) }
            }
    }

    @Nested
    @DisplayName("deleteLead")
    inner class DeleteLeadTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Lead selecionado é excluído`() =
            runTest {
                val sut = createSut()
                val leadId = faker.random.nextLong(bound = 9999)
                sut.onDeleteLead(leadId)
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.deleteLead()

                    awaitItem() shouldBeEqualTo LeadsViewModel.UiEvent.ShowDeleteSuccessSnackbar
                }
                sut.leadToDelete.value.shouldBeNull()
                coVerify { clienteWriteDataSourceMock.delete(leadId) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Lead selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSut()
                val leadId = faker.random.nextLong(bound = 9999)
                sut.onDeleteLead(leadId)
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deleteLead()

                    awaitItem() shouldBeEqualTo
                            LeadsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
                sut.leadToDelete.value shouldBeEqualTo leadId
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que nenhum Lead está selecionado`() =
            runTest {
                val sut = createSut()
                sut.onDeleteLead(null)

                sut.deleteLead()

                coVerify(exactly = 0) { clienteWriteDataSourceMock.delete(any()) }
            }
    }

    private fun createClienteReadModel() = ClienteReadModel(
        id = faker.random.nextLong(bound = 9999),
        name = faker.random.nextUUID(),
        city = faker.random.nextUUID(),
        hasUnfinishedAtendimento = true,
        hospedesCount = 2,
        hasUnfinishedStay = false,
        currentStayId = null,
    )
}
