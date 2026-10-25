package com.fstengineering.leasilycrm.proprietarioRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.SharedStorageDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioFileReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.useCases.WriteProprietarioUseCase
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ProprietarioRegistrationViewModelTest {
    private val writeProprietarioMock: WriteProprietarioUseCase = mockk(relaxed = true)
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

    private fun createSut(route: Destinations.ProprietarioRegistration) =
        ProprietarioRegistrationViewModel(
            route = route,
            writeProprietario = writeProprietarioMock,
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
