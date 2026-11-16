package com.fstengineering.leasilycrm.clienteRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.clienteRegistration.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteRegistrationReadDataSource
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteField
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ClienteRegistrationViewModelTest {
    private val clienteWriteDataSourceMock: ClienteWriteDataSource = mockk(relaxed = true)
    private val clienteRegistrationReadDataSourceMock: ClienteRegistrationReadDataSource =
        mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        coEvery { clienteRegistrationReadDataSourceMock.getTags() } returns
                AppResult.Success(emptyList())
        coEvery { clienteRegistrationReadDataSourceMock.getCities() } returns
                AppResult.Success(emptyList())
        coEvery { clienteRegistrationReadDataSourceMock.getContactOrigins() } returns
                AppResult.Success(emptyList())
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve preencher formulário com Cliente quando a tela de edição é aberta`() = runTest {
            val clienteId = faker.random.nextLong(bound = 9999)
            val cliente = createClienteReadModel(id = clienteId)
            coEvery { clienteRegistrationReadDataSourceMock.getCliente(clienteId) } returns
                    AppResult.Success(cliente)
            val sut = createSut(route = Destinations.ClienteRegistration(clienteId))

            sut.draftFormUiState.test {
                awaitItem() shouldBeEqualTo ClienteDraftFormUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ClienteDraftFormUiState.Loaded(
                    id = cliente.id,
                    createDateTime = cliente.createDateTime,
                    tag = cliente.tag,
                    name = cliente.name,
                    city = cliente.city,
                    identityDocument = cliente.identityDocument,
                    phone = cliente.phone,
                    email = cliente.email,
                    contactOrigin = cliente.contactOrigin,
                    observations = cliente.observations,
                    isLead = cliente.isLead,
                )
            }
        }

        @Test
        fun `Deve iniciar formulário vazio quando a tela de cadastro é aberta`() = runTest {
            val sut = createSut(route = Destinations.ClienteRegistration(clienteId = null))

            sut.draftFormUiState.test {
                awaitItem() shouldBeEqualTo ClienteDraftFormUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ClienteDraftFormUiState.Loaded.Empty
            }
            coVerify(exactly = 0) { clienteRegistrationReadDataSourceMock.getCliente(any()) }
        }

        @Test
        fun `Deve sugerir etiquetas, cidades e Origens do contato já usadas quando a tela é aberta`() =
            runTest {
                coEvery { clienteRegistrationReadDataSourceMock.getTags() } returns
                        AppResult.Success(listOf("VIP"))
                coEvery { clienteRegistrationReadDataSourceMock.getCities() } returns
                        AppResult.Success(listOf("Florianópolis"))
                coEvery { clienteRegistrationReadDataSourceMock.getContactOrigins() } returns
                        AppResult.Success(listOf("Instagram"))
                val sut = createSut(route = Destinations.ClienteRegistration(clienteId = null))

                sut.init()

                sut.pastTags.value shouldBeEqualTo persistentListOf("VIP")
                sut.pastCities.value shouldBeEqualTo persistentListOf("Florianópolis")
                sut.pastContactOrigins.value shouldBeEqualTo persistentListOf("Instagram")
            }

        @Test
        fun `Deve carregar o Cliente uma única vez quando a tela chama init novamente`() = runTest {
            val clienteId = faker.random.nextLong(bound = 9999)
            coEvery { clienteRegistrationReadDataSourceMock.getCliente(clienteId) } returns
                    AppResult.Success(createClienteReadModel(id = clienteId))
            val sut = createSut(route = Destinations.ClienteRegistration(clienteId))

            sut.init()
            sut.init()

            coVerify(exactly = 1) { clienteRegistrationReadDataSourceMock.getCliente(clienteId) }
        }
    }

    @Nested
    @DisplayName("saveCliente")
    inner class SaveClienteTests {
        @Test
        fun `Deve rejeitar Cliente sem persisti-lo quando cadastrado, dado que o nome está ausente`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm().copy(name = null))

                sut.uiEvents.test {
                    sut.saveCliente()

                    awaitItem() shouldBeEqualTo
                            ClienteRegistrationViewModel.UiEvent.ScrollToField(ClienteField.Name)
                }
                sut.formErrors.value.errors[ClienteField.Name].shouldNotBeNull()
                coVerify(exactly = 0) { clienteWriteDataSourceMock.save(any()) }
            }

        @Test
        fun `Deve informar falha de persistência quando cadastrado, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm())
                coEvery { clienteWriteDataSourceMock.save(any()) } returns
                        AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.saveCliente()

                    awaitItem() shouldBeEqualTo
                            ClienteRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                                ApplicationError.Unknown
                            )
                }
            }

        @Test
        fun `Deve persistir Cliente quando cadastrado, dado que os dados são válidos`() = runTest {
            val form = createValidForm()
            val sut = createSutWithNewForm(form)
            coEvery { clienteWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)

            sut.uiEvents.test {
                sut.saveCliente()

                awaitItem() shouldBeEqualTo ClienteRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                    messageId = R.string.create_lead_success_message,
                )
            }
            coVerify {
                clienteWriteDataSourceMock.save(
                    match { it.name.value == form.name && it.email?.value == form.email },
                )
            }
        }

        @Test
        fun `Deve avisar que nada mudou sem persistir quando editado, dado que o formulário não foi alterado`() =
            runTest {
                val clienteId = faker.random.nextLong(bound = 9999)
                coEvery { clienteRegistrationReadDataSourceMock.getCliente(clienteId) } returns
                        AppResult.Success(createClienteReadModel(id = clienteId))
                val sut = createSut(route = Destinations.ClienteRegistration(clienteId))
                sut.init()

                sut.uiEvents.test {
                    sut.saveCliente()

                    awaitItem() shouldBeEqualTo ClienteRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                        messageId = R.string.no_data_changed_success_message,
                    )
                }
                coVerify(exactly = 0) { clienteWriteDataSourceMock.save(any()) }
            }

        @Test
        fun `Deve confirmar edição de Lead quando editado, dado que os dados são válidos`() =
            runTest {
                val clienteId = faker.random.nextLong(bound = 9999)
                val cliente = createClienteReadModel(id = clienteId, isLead = true)
                coEvery { clienteRegistrationReadDataSourceMock.getCliente(clienteId) } returns
                        AppResult.Success(cliente)
                coEvery { clienteWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)
                val sut = createSut(route = Destinations.ClienteRegistration(clienteId))
                sut.init()
                val loaded = sut.draftFormUiState.value as ClienteDraftFormUiState.Loaded
                sut.onClienteFormChange(loaded.copy(city = faker.random.nextUUID()))

                sut.uiEvents.test {
                    sut.saveCliente()

                    awaitItem() shouldBeEqualTo ClienteRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                        messageId = R.string.edit_lead_success_message,
                    )
                }
                coVerify { clienteWriteDataSourceMock.save(match { it.id == clienteId }) }
            }

        @Test
        fun `Deve confirmar edição de Hospede quando editado, dado que os dados são válidos`() =
            runTest {
                val clienteId = faker.random.nextLong(bound = 9999)
                val cliente = createClienteReadModel(id = clienteId, isLead = false)
                coEvery { clienteRegistrationReadDataSourceMock.getCliente(clienteId) } returns
                        AppResult.Success(cliente)
                coEvery { clienteWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)
                val sut = createSut(route = Destinations.ClienteRegistration(clienteId))
                sut.init()
                val loaded = sut.draftFormUiState.value as ClienteDraftFormUiState.Loaded
                sut.onClienteFormChange(loaded.copy(city = faker.random.nextUUID()))

                sut.uiEvents.test {
                    sut.saveCliente()

                    awaitItem() shouldBeEqualTo ClienteRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                        messageId = R.string.edit_tenant_success_message,
                    )
                }
            }
    }

    @Nested
    @DisplayName("onExit")
    inner class OnExitTests {
        @Test
        fun `Deve sair sem confirmar quando o cadastro é abandonado, dado que nenhum campo foi preenchido`() =
            runTest {
                val sut = createSutWithNewForm(ClienteDraftFormUiState.Loaded.Empty)

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo ClienteRegistrationViewModel.UiEvent.CloseScreen
                }
            }

        @Test
        fun `Deve pedir confirmação quando o cadastro é abandonado, dado que há campo preenchido`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm())

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                            ClienteRegistrationViewModel.UiEvent.ShowConfirmLeaveDialog
                }
            }
    }

    private fun createSutWithNewForm(form: ClienteDraftFormUiState.Loaded) =
        createSut(route = Destinations.ClienteRegistration(clienteId = null)).also {
            it.init()
            it.onClienteFormChange(form)
        }

    private fun createValidForm() = ClienteDraftFormUiState.Loaded(
        name = faker.random.nextUUID(),
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        isLead = true,
    )

    private fun createSut(route: Destinations.ClienteRegistration) =
        ClienteRegistrationViewModel(
            route = route,
            clienteWriteDataSource = clienteWriteDataSourceMock,
            clienteRegistrationReadDataSource = clienteRegistrationReadDataSourceMock,
        )

    private fun createClienteReadModel(id: Long, isLead: Boolean = true) = ClienteReadModel(
        id = id,
        createDateTime = com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime.now(),
        tag = faker.random.nextUUID(),
        name = faker.random.nextUUID(),
        city = faker.random.nextUUID(),
        identityDocument = faker.random.nextUUID(),
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        contactOrigin = faker.random.nextUUID(),
        observations = faker.random.nextUUID(),
        isLead = isLead,
    )
}
