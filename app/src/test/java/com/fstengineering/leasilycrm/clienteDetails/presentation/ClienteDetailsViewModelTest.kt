package com.fstengineering.leasilycrm.clienteDetails.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoWriteDataSource
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.dataSources.ClienteDetailsReadDataSource
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsUiState
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ClienteDetailsViewModelTest {
    private val clienteDetailsReadDataSourceMock: ClienteDetailsReadDataSource =
        mockk(relaxed = true)
    private val clienteWriteDataSourceMock: ClienteWriteDataSource = mockk(relaxed = true)
    private val atendimentoWriteDataSourceMock: AtendimentoWriteDataSource = mockk(relaxed = true)

    private val clienteId = faker.random.nextLong(bound = 9999)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir o Cliente quando a tela de detalhes é aberta`() = runTest {
            val cliente = createClienteReadModel(isLead = true)
            every { clienteDetailsReadDataSourceMock.watchCliente(clienteId) } returns
                    flowOf(AppResult.Success(cliente))
            val sut = createSut()

            sut.init()

            val loaded = sut.uiState.value.shouldBeInstanceOf<ClienteDetailsUiState.Loaded>()
            loaded.id shouldBeEqualTo cliente.id
            loaded.name shouldBeEqualTo cliente.name
            loaded.isLead shouldBeEqualTo true
        }

        @Test
        fun `Deve voltar à tela anterior quando a tela de detalhes é aberta, dado que o Cliente não existe mais`() =
            runTest {
                every { clienteDetailsReadDataSourceMock.watchCliente(clienteId) } returns
                        flowOf(AppResult.Success(null))
                val sut = createSut()

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo ClienteDetailsViewModel.UiEvent.NavigateUp
                }
            }

        @Test
        fun `Deve informar falha quando a tela de detalhes é aberta, dado que o armazenamento local falha`() =
            runTest {
                every { clienteDetailsReadDataSourceMock.watchCliente(clienteId) } returns
                        flowOf(AppResult.Error(ApplicationError.Unknown))
                val sut = createSut()

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                            ClienteDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }

        @Test
        fun `Deve observar o Cliente uma única vez quando a tela chama init novamente`() = runTest {
            every { clienteDetailsReadDataSourceMock.watchCliente(clienteId) } returns
                    flowOf(AppResult.Success(createClienteReadModel(isLead = true)))
            val sut = createSut()

            sut.init()
            sut.init()

            verify(exactly = 1) { clienteDetailsReadDataSourceMock.watchCliente(clienteId) }
        }
    }

    @Nested
    @DisplayName("onDeleteCliente")
    inner class OnDeleteClienteTests {
        @Test
        fun `Deve excluir o Cliente e confirmar como Lead quando o Lead é excluído`() = runTest {
            val sut = createLoadedSut(createClienteReadModel(isLead = true))
            coEvery { clienteWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)

            sut.uiEvents.test {
                sut.onDeleteCliente()

                awaitItem() shouldBeEqualTo
                        ClienteDetailsViewModel.UiEvent.ShowClienteDeleteSnackbar(isLead = true)
            }
            coVerify { clienteWriteDataSourceMock.delete(clienteId) }
        }

        @Test
        fun `Deve excluir o Cliente e confirmar como Hospede quando o Hospede é excluído`() =
            runTest {
                val sut = createLoadedSut(createClienteReadModel(isLead = false))
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)

                sut.uiEvents.test {
                    sut.onDeleteCliente()

                    awaitItem() shouldBeEqualTo
                            ClienteDetailsViewModel.UiEvent.ShowClienteDeleteSnackbar(isLead = false)
                }
            }

        @Test
        fun `Deve informar falha quando o Cliente é excluído, dado que o armazenamento local falha`() =
            runTest {
                val sut = createLoadedSut(createClienteReadModel(isLead = true))
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.onDeleteCliente()

                    awaitItem() shouldBeEqualTo
                            ClienteDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }

        @Test
        fun `Deve voltar a observar o Cliente quando o Cliente é excluído, dado que o armazenamento local falha`() =
            runTest {
                val sut = createLoadedSut(createClienteReadModel(isLead = true))
                coEvery { clienteWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.Unknown)

                sut.onDeleteCliente()

                verify(exactly = 2) { clienteDetailsReadDataSourceMock.watchCliente(clienteId) }
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que o Cliente ainda não foi carregado`() =
            runTest {
                val sut = createSut()

                sut.onDeleteCliente()

                coVerify(exactly = 0) { clienteWriteDataSourceMock.delete(any()) }
            }
    }

    @Nested
    @DisplayName("deleteCurrentAtendimento")
    inner class DeleteCurrentAtendimentoTests {
        @Test
        fun `Deve confirmar exclusão quando o Atendimento atual é excluído`() = runTest {
            val atendimentoId = faker.random.nextLong(bound = 9999)
            val sut = createSut()
            coEvery { atendimentoWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)

            sut.uiEvents.test {
                sut.deleteCurrentAtendimento(atendimentoId)

                awaitItem() shouldBeEqualTo ClienteDetailsViewModel.UiEvent.ShowAtendimentoDeleteSnackbar
            }
            coVerify { atendimentoWriteDataSourceMock.delete(atendimentoId) }
        }

        @Test
        fun `Deve informar falha quando o Atendimento atual é excluído, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSut()
                coEvery { atendimentoWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.deleteCurrentAtendimento(faker.random.nextLong(bound = 9999))

                    awaitItem() shouldBeEqualTo
                            ClienteDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }
    }

    private fun createLoadedSut(cliente: ClienteReadModel): ClienteDetailsViewModel {
        every { clienteDetailsReadDataSourceMock.watchCliente(clienteId) } returns
                flowOf(AppResult.Success(cliente))

        return createSut().also { it.init() }
    }

    private fun createSut() = ClienteDetailsViewModel(
        route = Destinations.ClienteDetails(clienteId = clienteId),
        clienteDetailsReadDataSource = clienteDetailsReadDataSourceMock,
        clienteWriteDataSource = clienteWriteDataSourceMock,
        atendimentoWriteDataSource = atendimentoWriteDataSourceMock,
    )

    private fun createClienteReadModel(isLead: Boolean) = ClienteReadModel(
        id = clienteId,
        tag = null,
        name = faker.random.nextUUID(),
        city = null,
        identityDocument = null,
        phone = null,
        email = null,
        contactOrigin = null,
        observations = null,
        isLead = isLead,
        isOld = false,
        atendimentos = persistentListOf(),
        stays = persistentListOf(),
    )
}
