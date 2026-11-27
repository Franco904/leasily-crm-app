package com.fstengineering.leasilycrm.proprietarioRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.sharedStorage.SharedStorage
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioField
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ProprietarioRegistrationViewModelTest {
    private val proprietarioWriteDataSourceMock: ProprietarioWriteDataSource =
        mockk(relaxUnitFun = true)
    private val proprietarioRegistrationReadDataSourceMock: ProprietarioRegistrationReadDataSource =
        mockk(relaxUnitFun = true)
    private val sharedStorageMock: SharedStorage = mockk(relaxUnitFun = true)

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve preencher formulário com Proprietário quando a tela de edição é aberta`() =
            runTest {
                val proprietarioId = faker.random.nextLong(bound = 9999)
                val proprietario = createProprietarioReadModel(id = proprietarioId)
                val file = proprietario.files.single()
                coEvery { proprietarioRegistrationReadDataSourceMock.getProprietario(proprietarioId) } returns
                    AppResult.Success(proprietario)
                val sut = createSut(route = Destinations.ProprietarioRegistration(proprietarioId))

                sut.draftFormUiState.test {
                    awaitItem() shouldBeEqualTo ProprietarioDraftFormUiState.Loading

                    sut.init()

                    awaitItem() shouldBeEqualTo ProprietarioDraftFormUiState.Loaded(
                        id = proprietario.id,
                        createDateTime = proprietario.createDateTime,
                        name = proprietario.name,
                        phone = proprietario.phone,
                        email = proprietario.email,
                        paymentKey = proprietario.paymentKey,
                        files = persistentListOf(
                            ProprietarioFileUiState(
                                id = file.id,
                                uriString = file.uriString,
                                name = file.name
                            ),
                        ),
                    )
                }
            }

        @Test
        fun `Deve iniciar formulário vazio quando a tela de cadastro é aberta`() = runTest {
            val sut =
                createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null))

            sut.draftFormUiState.test {
                awaitItem() shouldBeEqualTo ProprietarioDraftFormUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ProprietarioDraftFormUiState.Loaded.Empty
            }
            coVerify(exactly = 0) { proprietarioRegistrationReadDataSourceMock.getProprietario(any()) }
        }
    }

    @Nested
    @DisplayName("saveProprietario")
    inner class SaveProprietarioTests {
        @Test
        fun `Deve rejeitar Proprietário sem persisti-lo quando cadastrado, dado que o nome está ausente`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm().copy(name = null))

                sut.uiEvents.test {
                    sut.saveProprietario()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.ScrollToField(
                            ProprietarioField.Name
                        )
                }
                sut.formErrors.value.errors[ProprietarioField.Name].shouldNotBeNull()
                coVerify(exactly = 0) { proprietarioWriteDataSourceMock.save(any()) }
            }

        @Test
        fun `Deve informar falha de persistência quando cadastrado, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm())
                coEvery { proprietarioWriteDataSourceMock.save(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)

                sut.uiEvents.test {
                    sut.saveProprietario()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                            ApplicationError.Unknown
                        )
                }
            }

        @Test
        fun `Deve persistir Proprietário quando cadastrado, dado que os dados são válidos`() =
            runTest {
                val form = createValidForm()
                val sut = createSutWithNewForm(form)
                coEvery { proprietarioWriteDataSourceMock.save(any()) } returns AppResult.Success(
                    Unit
                )

                sut.uiEvents.test {
                    sut.saveProprietario()

                    awaitItem() shouldBeEqualTo ProprietarioRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                        messageId = R.string.create_property_owner_success_message,
                    )
                }
                coVerify {
                    proprietarioWriteDataSourceMock.save(
                        match { it.name.value == form.name && it.email?.value == form.email },
                    )
                }
            }
    }

    @Nested
    @DisplayName("onProprietarioFormChange")
    inner class OnProprietarioFormChangeTests {
        @Test
        fun `Deve exibir os dados informados quando o formulário é alterado`() = runTest {
            val sut = createSutWithNewForm(createValidForm())
            val form = createValidForm()

            sut.onProprietarioFormChange(form)

            sut.draftFormUiState.value shouldBeEqualTo form
        }

        @Test
        fun `Deve limpar só o erro de nome quando ele é alterado, dado que há erros`() = runTest {
            val form = createInvalidForm()
            val sut = createSutWithNewForm(form)
            sut.saveProprietario()
            advanceTimeBy(200)

            sut.onProprietarioFormChange(form.copy(name = faker.random.nextUUID()))

            sut.formErrors.value.errors[ProprietarioField.Name].shouldBeNull()
            sut.formErrors.value.errors[ProprietarioField.Phone].shouldNotBeNull()
            sut.formErrors.value.errors[ProprietarioField.Email].shouldNotBeNull()
            sut.formErrors.value.errors[ProprietarioField.PaymentKey].shouldNotBeNull()
        }

        @Test
        fun `Deve limpar só o erro de telefone quando ele é alterado, dado que há erros`() =
            runTest {
                val form = createInvalidForm()
                val sut = createSutWithNewForm(form)
                sut.saveProprietario()
                advanceTimeBy(200)

                sut.onProprietarioFormChange(form.copy(phone = faker.random.nextUUID()))

                sut.formErrors.value.errors[ProprietarioField.Phone].shouldBeNull()
                sut.formErrors.value.errors[ProprietarioField.Name].shouldNotBeNull()
                sut.formErrors.value.errors[ProprietarioField.Email].shouldNotBeNull()
                sut.formErrors.value.errors[ProprietarioField.PaymentKey].shouldNotBeNull()
            }

        @Test
        fun `Deve limpar só o erro de e-mail quando ele é alterado, dado que há erros`() = runTest {
            val form = createInvalidForm()
            val sut = createSutWithNewForm(form)
            sut.saveProprietario()
            advanceTimeBy(200)

            sut.onProprietarioFormChange(form.copy(email = faker.random.nextUUID()))

            sut.formErrors.value.errors[ProprietarioField.Email].shouldBeNull()
            sut.formErrors.value.errors[ProprietarioField.Name].shouldNotBeNull()
            sut.formErrors.value.errors[ProprietarioField.Phone].shouldNotBeNull()
            sut.formErrors.value.errors[ProprietarioField.PaymentKey].shouldNotBeNull()
        }

        @Test
        fun `Deve limpar só o erro de chave de pagamento quando ela é alterada, dado que há erros`() =
            runTest {
                val form = createInvalidForm()
                val sut = createSutWithNewForm(form)
                sut.saveProprietario()
                advanceTimeBy(200)

                sut.onProprietarioFormChange(form.copy(paymentKey = faker.random.nextUUID()))

                sut.formErrors.value.errors[ProprietarioField.PaymentKey].shouldBeNull()
                sut.formErrors.value.errors[ProprietarioField.Name].shouldNotBeNull()
                sut.formErrors.value.errors[ProprietarioField.Phone].shouldNotBeNull()
                sut.formErrors.value.errors[ProprietarioField.Email].shouldNotBeNull()
            }

        @Test
        fun `Deve ignorar a alteração quando o formulário ainda não foi carregado`() = runTest {
            val sut =
                createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null))

            sut.onProprietarioFormChange(createValidForm())

            sut.draftFormUiState.value shouldBeEqualTo ProprietarioDraftFormUiState.Loading
        }
    }

    @Nested
    @DisplayName("onProprietarioFilePick")
    inner class OnProprietarioFilePickTests {
        @Test
        fun `Deve anexar o arquivo com o nome dele quando ele é escolhido`() = runTest {
            val form = createValidForm()
            val sut = createSutWithNewForm(form)
            val contentUriString = faker.random.nextUUID()
            val fileName = faker.random.nextUUID()
            coEvery { sharedStorageMock.getFileName(contentUriString) } returns fileName

            sut.onProprietarioFilePick(contentUriString, defaultFileName = faker.random.nextUUID())

            sut.draftFormUiState.value shouldBeEqualTo form.copy(
                files = persistentListOf(
                    ProprietarioFileUiState(uriString = contentUriString, name = fileName),
                ),
            )
        }

        @Test
        fun `Deve anexar o arquivo com o nome padrão quando ele é escolhido, dado que o nome é desconhecido`() =
            runTest {
                val form = createValidForm()
                val sut = createSutWithNewForm(form)
                val contentUriString = faker.random.nextUUID()
                val defaultFileName = faker.random.nextUUID()
                coEvery { sharedStorageMock.getFileName(contentUriString) } returns null

                sut.onProprietarioFilePick(contentUriString, defaultFileName = defaultFileName)

                sut.draftFormUiState.value shouldBeEqualTo form.copy(
                    files = persistentListOf(
                        ProprietarioFileUiState(
                            uriString = contentUriString,
                            name = defaultFileName,
                        ),
                    ),
                )
            }

        @Test
        fun `Deve ignorar o arquivo escolhido quando o formulário ainda não foi carregado`() =
            runTest {
                val sut =
                    createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null))

                sut.onProprietarioFilePick(
                    faker.random.nextUUID(),
                    defaultFileName = faker.random.nextUUID(),
                )

                sut.draftFormUiState.value shouldBeEqualTo ProprietarioDraftFormUiState.Loading
                coVerify(exactly = 0) { sharedStorageMock.getFileName(any()) }
            }
    }

    @Nested
    @DisplayName("onExit")
    inner class OnExitTests {
        @Test
        fun `Deve pedir confirmação quando a saída ocorre, dado que o novo cadastro foi preenchido`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm())

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.ShowConfirmLeaveDialog
                }
            }

        @Test
        fun `Deve sair sem confirmação quando a saída ocorre, dado que o novo cadastro está vazio`() =
            runTest {
                val sut = createSutWithNewForm(ProprietarioDraftFormUiState.Loaded.Empty)

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.CloseScreen
                }
            }

        @Test
        fun `Deve sair sem confirmação quando a saída ocorre, dado que o Proprietário editado não mudou`() =
            runTest {
                val proprietarioId = faker.random.nextLong(bound = 9999)
                coEvery {
                    proprietarioRegistrationReadDataSourceMock.getProprietario(proprietarioId)
                } returns AppResult.Success(createProprietarioReadModel(proprietarioId))
                val sut = createSut(route = Destinations.ProprietarioRegistration(proprietarioId))
                sut.init()

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.CloseScreen
                }
            }

        @Test
        fun `Deve pedir confirmação quando a saída ocorre, dado que o Proprietário editado mudou`() =
            runTest {
                val proprietarioId = faker.random.nextLong(bound = 9999)
                coEvery {
                    proprietarioRegistrationReadDataSourceMock.getProprietario(proprietarioId)
                } returns AppResult.Success(createProprietarioReadModel(proprietarioId))
                val sut = createSut(route = Destinations.ProprietarioRegistration(proprietarioId))
                sut.init()
                val storedForm = sut.draftFormUiState.value as ProprietarioDraftFormUiState.Loaded
                sut.onProprietarioFormChange(storedForm.copy(name = faker.random.nextUUID()))

                sut.uiEvents.test {
                    sut.onExit()

                    awaitItem() shouldBeEqualTo
                        ProprietarioRegistrationViewModel.UiEvent.ShowConfirmLeaveDialog
                }
            }
    }

    private fun createSutWithNewForm(form: ProprietarioDraftFormUiState.Loaded) =
        createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null)).also {
            it.init()
            it.onProprietarioFormChange(form)
        }

    private fun createInvalidForm() = createValidForm().copy(
        name = null,
        phone = "9".repeat(21),
        email = "invalido",
        paymentKey = "a".repeat(121),
    )

    private fun createValidForm() = ProprietarioDraftFormUiState.Loaded(
        name = faker.random.nextUUID(),
        phone = faker.random.nextInt(bound = 999999999).toString(),
        email = "${faker.random.nextUUID()}@leasily.com",
        paymentKey = faker.random.nextUUID(),
    )

    private fun createSut(route: Destinations.ProprietarioRegistration) =
        ProprietarioRegistrationViewModel(
            route = route,
            proprietarioWriteDataSource = proprietarioWriteDataSourceMock,
            proprietarioRegistrationReadDataSource = proprietarioRegistrationReadDataSourceMock,
            sharedStorage = sharedStorageMock,
        )

    private fun createProprietarioReadModel(id: Long) = ProprietarioReadModel(
        id = id,
        createDateTime = null,
        updateDateTime = null,
        name = faker.random.nextUUID(),
        phone = faker.random.nextUUID(),
        email = faker.random.nextUUID(),
        paymentKey = faker.random.nextUUID(),
        files = persistentListOf(
            ProprietarioFileReadModel(
                id = faker.random.nextLong(bound = 9999),
                uriString = faker.random.nextUUID(),
                name = faker.random.nextUUID(),
            ),
        ),
    )
}
