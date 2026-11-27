package com.fstengineering.leasilycrm.clientes.presentation.hospedes

import app.cash.turbine.test
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clientes.infra.dataSources.ClientesReadDataSource
import com.fstengineering.leasilycrm.clientes.infra.localStorage.ClientesLocalStorage
import com.fstengineering.leasilycrm.clientes.presentation.models.HospedeFilterType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.presentation.models.ClienteOrderType
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.clearFromStore
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.milliseconds

@ViewModelCoroutineTest
class HospedesViewModelTest {
    private val clientesReadDataSourceMock: ClientesReadDataSource = mockk(relaxUnitFun = true)
    private val clienteWriteDataSourceMock: ClienteWriteDataSource = mockk(relaxUnitFun = true)
    private val clientesLocalStorageMock: ClientesLocalStorage =
        mockk(relaxUnitFun = true)

    private val sut = HospedesViewModel(
        clientesReadDataSource = clientesReadDataSourceMock,
        clienteWriteDataSource = clienteWriteDataSourceMock,
        clientesLocalStorage = clientesLocalStorageMock,
    )

    @BeforeEach
    fun setUp() {
        coEvery { clientesLocalStorageMock.getHospedesOrderTypeId() } returns
            AppResult.Success(ClienteOrderType.Name.id)
        every { clientesReadDataSourceMock.watchClientes(any(), any(), any()) } returns
            flowOf(AppResult.Success(emptyList()))
        coEvery { clientesLocalStorageMock.saveHospedesOrderTypeId(any()) } returns
            AppResult.Success(Unit)
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Hospedes com Estadia quando a listagem é aberta`() = runTest {
            val hospede = createClienteReadModel()
            every {
                clientesReadDataSourceMock.watchClientes(
                    status = ClienteStatus.Hospede,
                    matching = null,
                    orderTypeId = ClienteOrderType.Name.id,
                )
            } returns flowOf(AppResult.Success(listOf(hospede)))

            sut.init()
            advanceTimeBy(600.milliseconds)
            runCurrent()

            sut.hospedes.value.single().cliente.id shouldBeEqualTo hospede.id
            sut.isLoading.value shouldBeEqualTo false
        }

        @Test
        fun `Deve informar falha quando a listagem é aberta, dado que o armazenamento local falha`() =
            runTest {
                every { clientesReadDataSourceMock.watchClientes(any(), any(), any()) } returns
                    flowOf(AppResult.Error(ApplicationError.Unknown))

                sut.uiEvents.test {
                    sut.init()
                    advanceTimeBy(600.milliseconds)
                    runCurrent()

                    awaitItem() shouldBeEqualTo
                        HospedesViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }
    }

    @Nested
    @DisplayName("onHospedeFilterTypeChange")
    inner class OnHospedeFilterTypeChangeTests {
        @Test
        fun `Deve listar Hospedes antigos quando o filtro é trocado para antigos`() = runTest {
            val hospedeAntigo = createClienteReadModel()
            every {
                clientesReadDataSourceMock.watchClientes(
                    status = ClienteStatus.HospedeAntigo,
                    matching = null,
                    orderTypeId = ClienteOrderType.Name.id,
                )
            } returns flowOf(AppResult.Success(listOf(hospedeAntigo)))
            sut.init()
            advanceTimeBy(600.milliseconds)
            runCurrent()

            sut.onHospedeFilterTypeChange(HospedeFilterType.Antigo)

            sut.hospedes.value.single().cliente.id shouldBeEqualTo hospedeAntigo.id
        }
    }

    @Nested
    @DisplayName("onOrderTypeChange")
    inner class OnOrderTypeChangeTests {
        @Test
        fun `Deve guardar a preferência de ordenação dos Hospedes quando a ordenação é trocada`() =
            runTest {
                sut.init()
                advanceTimeBy(600.milliseconds)
                runCurrent()

                sut.onOrderTypeChange(ClienteOrderType.LastUpdateDate)

                coVerify {
                    clientesLocalStorageMock.saveHospedesOrderTypeId(ClienteOrderType.LastUpdateDate.id)
                }
                coVerify(exactly = 0) { clientesLocalStorageMock.saveLeadsOrderTypeId(any()) }
                verify {
                    clientesReadDataSourceMock.watchClientes(
                        status = ClienteStatus.Hospede,
                        matching = null,
                        orderTypeId = ClienteOrderType.LastUpdateDate.id,
                    )
                }
            }
    }

    @Nested
    @DisplayName("deleteHospede")
    inner class DeleteHospedeTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Hospede selecionado é excluído`() =
            runTest {
                val hospedeId = faker.random.nextLong(bound = 9999)
                sut.onDeleteHospede(hospedeId)
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.deleteHospede()

                    awaitItem() shouldBeEqualTo HospedesViewModel.UiEvent.ShowDeleteSuccessSnackbar
                }
                sut.hospedeToDelete.value.shouldBeNull()
                coVerify { clienteWriteDataSourceMock.delete(hospedeId) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Hospede selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val hospedeId = faker.random.nextLong(bound = 9999)
                sut.onDeleteHospede(hospedeId)
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deleteHospede()

                    awaitItem() shouldBeEqualTo
                        HospedesViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
                sut.hospedeToDelete.value shouldBeEqualTo hospedeId
            }
    }

    @Nested
    @DisplayName("onSearchInputChange")
    inner class OnSearchInputChangeTests {
        @Test
        fun `Deve listar os Hóspedes que correspondem à busca quando o texto é digitado`() =
            runTest {
                val searchInput = faker.random.nextUUID()
                val hospede = createClienteReadModel()
                every {
                    clientesReadDataSourceMock.watchClientes(
                        status = ClienteStatus.Hospede,
                        matching = searchInput,
                        orderTypeId = ClienteOrderType.Name.id,
                    )
                } returns flowOf(AppResult.Success(listOf(hospede)))
                sut.init()
                advanceTimeBy(600)

                sut.onSearchInputChange(searchInput)
                advanceTimeBy(600)

                sut.hospedes.value.single().cliente.id shouldBeEqualTo hospede.id
            }

        @Test
        fun `Deve listar sem filtro de texto quando a busca é apagada`() = runTest {
            val hospede = createClienteReadModel()
            every {
                clientesReadDataSourceMock.watchClientes(
                    status = ClienteStatus.Hospede,
                    matching = null,
                    orderTypeId = ClienteOrderType.Name.id,
                )
            } returns flowOf(AppResult.Success(listOf(hospede)))
            sut.init()
            sut.onSearchInputChange(faker.random.nextUUID())
            advanceTimeBy(600)

            sut.onSearchInputChange("")
            advanceTimeBy(600)

            sut.hospedes.value.single().cliente.id shouldBeEqualTo hospede.id
        }
    }

    @Nested
    @DisplayName("onDeleteHospede")
    inner class OnDeleteHospedeTests {
        @Test
        fun `Deve selecionar o Hóspede quando a exclusão é solicitada`() = runTest {
            val id = faker.random.nextLong(bound = 9999)

            sut.onDeleteHospede(id)

            sut.hospedeToDelete.value shouldBeEqualTo id
        }

        @Test
        fun `Deve descartar a seleção quando a exclusão é cancelada`() = runTest {
            sut.onDeleteHospede(faker.random.nextLong(bound = 9999))

            sut.onDeleteHospede(null)

            sut.hospedeToDelete.value.shouldBeNull()
        }
    }

    @Nested
    @DisplayName("onCleared")
    inner class OnClearedTests {
        @Test
        fun `Deve encerrar os eventos da tela quando ela é descartada`() = runTest {
            sut.clearFromStore()

            sut.uiEvents.test { awaitComplete() }
        }
    }

    private fun createClienteReadModel() = ClienteReadModel(
        id = faker.random.nextLong(bound = 9999),
        name = faker.random.nextUUID(),
        city = faker.random.nextUUID(),
        hasUnfinishedAtendimento = false,
        hospedesCount = null,
        hasUnfinishedStay = true,
        currentStayId = faker.random.nextLong(bound = 9999),
    )
}
