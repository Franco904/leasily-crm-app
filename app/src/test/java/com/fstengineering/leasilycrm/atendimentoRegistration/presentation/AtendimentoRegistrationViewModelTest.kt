package com.fstengineering.leasilycrm.atendimentoRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoRegistrationReadDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoWriteDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.InteracaoDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.providers.AppDateProviderImpl
import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ClienteSharedReadDataSource
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class AtendimentoRegistrationViewModelTest {
    private val atendimentoRegistrationReadDataSourceMock: AtendimentoRegistrationReadDataSource =
        mockk(relaxUnitFun = true)
    private val clienteSharedReadDataSourceMock: ClienteSharedReadDataSource =
        mockk(relaxUnitFun = true)
    private val atendimentoWriteDataSourceMock: AtendimentoWriteDataSource =
        mockk(relaxUnitFun = true)

    private val clienteId = 7L

    @BeforeEach
    fun setUp() {
        coEvery { atendimentoRegistrationReadDataSourceMock.getNeighborhoodNames() } returns
            AppResult.Success(emptyList())
        coEvery { clienteSharedReadDataSourceMock.getName(clienteId) } returns
            AppResult.Success("Maria")
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir formulário vazio do Cliente quando Atendimento é cadastrado`() = runTest {
            val sut = createSut()

            sut.draftFormUiState.test {
                awaitItem() shouldBeEqualTo AtendimentoDraftFormUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo
                    AtendimentoDraftFormUiState.Loaded.empty(clienteId, clienteName = "Maria")
            }
        }

        @Test
        fun `Deve exibir Atendimento armazenado quando Atendimento é editado`() = runTest {
            val atendimentoId = 3L
            coEvery { atendimentoRegistrationReadDataSourceMock.getAtendimento(atendimentoId) } returns
                AppResult.Success(
                    createAtendimentoReadModel(
                        atendimentoId,
                        observations = "Quer varanda"
                    )
                )
            val sut = createSut(atendimentoId = atendimentoId)

            sut.draftFormUiState.test {
                awaitItem() shouldBeEqualTo AtendimentoDraftFormUiState.Loading

                sut.init()

                val form = awaitItem().shouldBeInstanceOf<AtendimentoDraftFormUiState.Loaded>()
                form.id shouldBeEqualTo atendimentoId
                form.observations shouldBeEqualTo "Quer varanda"
            }
        }

        @Test
        fun `Deve informar falha quando Atendimento é editado, dado que o armazenamento local falha`() =
            runTest {
                val atendimentoId = 3L
                coEvery { atendimentoRegistrationReadDataSourceMock.getAtendimento(atendimentoId) } returns
                    AppResult.Error(ApplicationError.Unknown)
                val sut = createSut(atendimentoId = atendimentoId)

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                            ApplicationError.Unknown,
                        )
                }
            }

        @Test
        fun `Deve carregar dados uma única vez quando a tela é aberta mais de uma vez`() = runTest {
            val sut = createSut()

            sut.init()
            sut.init()

            coVerify(exactly = 1) { clienteSharedReadDataSourceMock.getName(clienteId) }
        }
    }

    @Nested
    @DisplayName("saveAtendimento")
    inner class SaveAtendimentoTests {
        @Test
        fun `Deve rejeitar Atendimento sem persisti-lo quando cadastrado, dado que a Interação não tem título`() =
            runTest {
                val sut = createSutWithNewForm { form ->
                    form.copy(
                        interacoes = persistentListOf(
                            InteracaoDraftFormUiState.Loaded(
                                id = null,
                                date = AppDateProviderImpl().now(),
                                title = "",
                                description = null,
                            ),
                        ),
                    )
                }

                sut.uiEvents.test {
                    sut.saveAtendimento()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                            ApplicationError.Unknown,
                        )
                }
                coVerify(exactly = 0) { atendimentoWriteDataSourceMock.save(any()) }
            }

        @Test
        fun `Deve informar falha de persistência quando cadastrado, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSutWithNewForm()
                coEvery { atendimentoWriteDataSourceMock.save(any()) } returns
                    AppResult.Error(ApplicationError.NoInternet)

                sut.uiEvents.test {
                    sut.saveAtendimento()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                            ApplicationError.NoInternet,
                        )
                }
            }

        @Test
        fun `Deve persistir Atendimento do Cliente quando cadastrado, dado que os dados são válidos`() =
            runTest {
                val sut = createSutWithNewForm { it.copy(adultsCount = 2) }
                coEvery { atendimentoWriteDataSourceMock.save(any()) } returns AppResult.Success(
                    Unit
                )

                sut.uiEvents.test {
                    sut.saveAtendimento()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                            messageId = R.string.create_assistance_success_message,
                        )
                }
                coVerify {
                    atendimentoWriteDataSourceMock.save(
                        match { it.clienteId == clienteId && it.adultsCount.value == 2 && !it.isFinished },
                    )
                }
            }

        @Test
        fun `Deve persistir Atendimento quando editado, dado que os dados são válidos`() = runTest {
            val atendimentoId = 3L
            coEvery { atendimentoRegistrationReadDataSourceMock.getAtendimento(atendimentoId) } returns
                AppResult.Success(createAtendimentoReadModel(atendimentoId))
            coEvery { atendimentoWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)
            val sut = createSut(atendimentoId = atendimentoId)
            sut.init()
            sut.onAtendimentoFormChange(sut.loadedForm().copy(kidsCount = 2))

            sut.uiEvents.test {
                sut.saveAtendimento()

                awaitItem() shouldBeEqualTo
                    AtendimentoRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                        messageId = R.string.edit_assistance_success_message,
                    )
            }
            coVerify {
                atendimentoWriteDataSourceMock.save(
                    match { it.id == atendimentoId && it.kidsCount.value == 2 },
                )
            }
        }

        @Test
        fun `Deve informar que nada mudou sem persistir quando editado, dado que o formulário não foi alterado`() =
            runTest {
                val atendimentoId = 3L
                coEvery { atendimentoRegistrationReadDataSourceMock.getAtendimento(atendimentoId) } returns
                    AppResult.Success(createAtendimentoReadModel(atendimentoId))
                val sut = createSut(atendimentoId = atendimentoId)
                sut.init()

                sut.uiEvents.test {
                    sut.saveAtendimento()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                            messageId = R.string.no_data_changed_success_message,
                        )
                }
                coVerify(exactly = 0) { atendimentoWriteDataSourceMock.save(any()) }
            }
    }

    @Nested
    @DisplayName("onExit")
    inner class OnExitTests {
        @Test
        fun `Deve pedir confirmação quando a saída ocorre, dado que o formulário foi alterado`() =
            runTest {
                val sut = createSutWithNewForm()
                sut.onAtendimentoFormChange(sut.loadedForm().copy(observations = "Mudou"))

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationViewModel.UiEvent.Exit(needConfirmation = true)
                }
            }
    }

    private suspend fun createSutWithNewForm(
        edit: (AtendimentoDraftFormUiState.Loaded) -> AtendimentoDraftFormUiState.Loaded = { it },
    ): AtendimentoRegistrationViewModel = createSut().also { sut ->
        sut.init()
        sut.onAtendimentoFormChange(edit(sut.loadedForm()))
    }

    private suspend fun AtendimentoRegistrationViewModel.loadedForm() =
        draftFormUiState.filterIsInstance<AtendimentoDraftFormUiState.Loaded>().first()

    private fun createSut(atendimentoId: Long? = null) = AtendimentoRegistrationViewModel(
        route = Destinations.AtendimentoRegistration(
            atendimentoId = atendimentoId,
            clienteId = clienteId,
        ),
        atendimentoRegistrationReadDataSource = atendimentoRegistrationReadDataSourceMock,
        clienteSharedReadDataSource = clienteSharedReadDataSourceMock,
        atendimentoWriteDataSource = atendimentoWriteDataSourceMock,
    )

    private fun createAtendimentoReadModel(
        id: Long,
        observations: String? = null,
    ) = AtendimentoReadModel(
        id = id,
        clienteId = clienteId,
        clienteName = faker.random.nextUUID(),
        createDateTime = AppDateTimeProviderImpl().now(),
        preferredStartDate = null,
        preferredEndDate = null,
        hasFlexibilityForPeriod = false,
        maxAcceptedNightlyRate = null,
        adultsCount = 1,
        kidsCount = 0,
        babiesCount = 0,
        animalsCount = 0,
        bedroomsCount = 0,
        enSuitesCount = 0,
        doubleBedsCount = 0,
        singleBedsCount = 0,
        bathroomsCount = 0,
        parkingSpotsCount = 0,
        preferredSeaProximityTypeId = null,
        preferredDistanceToSea = null,
        preferredNeighborhoods = persistentListOf(),
        preferredImovelCharacteristics = persistentListOf(),
        observations = observations,
        imoveisEnviados = persistentListOf(),
        interacoes = persistentListOf(),
    )
}
