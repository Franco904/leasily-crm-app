package com.fstengineering.leasilycrm.proprietarioRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.SharedStorageDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioField
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
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
class ProprietarioRegistrationViewModelTest {
    private val proprietarioWriteDataSourceMock: ProprietarioWriteDataSource = mockk(relaxed = true)
    private val proprietarioRegistrationReadDataSourceMock: ProprietarioRegistrationReadDataSource =
        mockk(relaxed = true)
    private val sharedStorageDataSourceMock: SharedStorageDataSource = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

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
                            ProprietarioFileUiState(id = file.id, uriString = file.uriString, name = file.name),
                        ),
                    )
                }
            }

        @Test
        fun `Deve iniciar formulário vazio quando a tela de cadastro é aberta`() = runTest {
            val sut = createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null))

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
                        ProprietarioRegistrationViewModel.UiEvent.ScrollToField(ProprietarioField.Name)
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
                        ProprietarioRegistrationViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown)
                }
            }

        @Test
        fun `Deve persistir Proprietário quando cadastrado, dado que os dados são válidos`() =
            runTest {
                val form = createValidForm()
                val sut = createSutWithNewForm(form)
                coEvery { proprietarioWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)

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

    private fun createSutWithNewForm(form: ProprietarioDraftFormUiState.Loaded) =
        createSut(route = Destinations.ProprietarioRegistration(proprietarioId = null)).also {
            it.init()
            it.onProprietarioFormChange(form)
        }

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
            sharedStorageDataSource = sharedStorageDataSourceMock,
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
