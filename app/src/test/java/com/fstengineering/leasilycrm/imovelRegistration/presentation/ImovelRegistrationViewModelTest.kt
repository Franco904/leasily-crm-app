package com.fstengineering.leasilycrm.imovelRegistration.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.providers.AppDateTimeProviderImpl
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelRegistrationReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ProprietarioReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ImovelRegistrationViewModelTest {
    private val imovelRegistrationReadDataSourceMock: ImovelRegistrationReadDataSource =
        mockk(relaxed = true)
    private val imovelWriteDataSourceMock: ImovelWriteDataSource = mockk(relaxed = true)
    private val proprietarioReadDataSourceMock: ProprietarioReadDataSource = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Nested
    @DisplayName("saveImovel")
    inner class SaveImovelTests {
        @Test
        fun `Deve rejeitar Imóvel sem persisti-lo quando cadastrado, dado que o bairro está ausente`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm().copy(neighborhood = null))

                sut.uiEvents.test {
                    sut.saveImovel()

                    awaitItem() shouldBeEqualTo
                            ImovelRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                                ApplicationError.Unknown
                            )
                }
                coVerify(exactly = 0) { imovelWriteDataSourceMock.save(any()) }
            }

        @Test
        fun `Deve informar falha de persistência quando cadastrado, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSutWithNewForm(createValidForm())
                coEvery { imovelWriteDataSourceMock.save(any()) } returns
                        AppResult.Error(ApplicationError.NoInternet)

                sut.uiEvents.test {
                    sut.saveImovel()

                    awaitItem() shouldBeEqualTo
                            ImovelRegistrationViewModel.UiEvent.ShowErrorBottomSheet(
                                ApplicationError.NoInternet
                            )
                }
            }

        @Test
        fun `Deve persistir Imóvel quando cadastrado, dado que os dados são válidos`() = runTest {
            val form = createValidForm()
            val sut = createSutWithNewForm(form)
            coEvery { imovelWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)

            sut.uiEvents.test {
                sut.saveImovel()

                awaitItem() shouldBeEqualTo ImovelRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                    messageId = R.string.create_property_success_message,
                )
            }
            coVerify {
                imovelWriteDataSourceMock.save(
                    match {
                        it.proprietarioId == form.proprietario?.id &&
                                it.neighborhood == form.neighborhood &&
                                it.streetAddress == form.streetAddress
                    },
                )
            }
        }

        @Test
        fun `Deve persistir Imóvel quando editado, dado que os dados são válidos`() = runTest {
            val imovelId = faker.random.nextLong(bound = 9999)
            val storedImovel = createImovelReadModel(id = imovelId)
            coEvery { imovelRegistrationReadDataSourceMock.getImovel(imovelId) } returns
                    AppResult.Success(storedImovel)
            coEvery { imovelWriteDataSourceMock.save(any()) } returns AppResult.Success(Unit)
            val sut = createSut(imovelId = imovelId)
            sut.init()
            val editedNeighborhood = faker.random.nextUUID()
            sut.onImovelFormChange(
                (sut.draftFormUiState.value as ImovelDraftFormUiState.Loaded)
                    .copy(neighborhood = editedNeighborhood),
            )

            sut.uiEvents.test {
                sut.saveImovel()

                awaitItem() shouldBeEqualTo ImovelRegistrationViewModel.UiEvent.CloseScreenWithSnackbar(
                    messageId = R.string.edit_property_success_message,
                )
            }
            coVerify {
                imovelWriteDataSourceMock.save(
                    match { it.id == imovelId && it.neighborhood == editedNeighborhood },
                )
            }
        }
    }

    private fun createSutWithNewForm(form: ImovelDraftFormUiState.Loaded) =
        createSut().also { it.onImovelFormChange(form) }

    private fun createSut(imovelId: Long? = null): ImovelRegistrationViewModel {
        every { proprietarioReadDataSourceMock.watchProprietarios() } returns emptyFlow()
        coEvery { imovelRegistrationReadDataSourceMock.getNeighborhoodNames() } returns
                AppResult.Success(emptyList())

        return ImovelRegistrationViewModel(
            route = Destinations.ImovelRegistration(imovelId = imovelId),
            imovelRegistrationReadDataSource = imovelRegistrationReadDataSourceMock,
            imovelWriteDataSource = imovelWriteDataSourceMock,
            proprietarioReadDataSource = proprietarioReadDataSourceMock,
        )
    }

    private fun createValidForm() = ImovelDraftFormUiState.Loaded(
        proprietario = ProprietarioUiState(
            id = faker.random.nextLong(bound = 9999),
            name = faker.random.nextUUID(),
        ),
        type = ImovelType.Apartment,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
    )

    private fun createImovelReadModel(id: Long) = ImovelReadModel(
        id = id,
        createDateTime = AppDateTimeProviderImpl().now(),
        proprietario = ProprietarioReadModel(
            id = faker.random.nextLong(bound = 9999),
            name = faker.random.nextUUID(),
        ),
        type = ImovelType.House,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
        cepPostalCode = null,
        number = null,
        addressLine2 = null,
        parkingSpotNumber = null,
        parkingBasementLevelNumber = null,
        hobbyBoxNumber = null,
        bedroomsCount = 0,
        enSuitesCount = 0,
        doubleBedsCount = 0,
        singleBedsCount = 0,
        bathroomsCount = 0,
        parkingSpotsCount = 0,
        airConditionersCount = 0,
        maxHospedesCount = 0,
        seaProximityTypeId = null,
        distanceToSea = null,
        includedItems = null,
        hasIncludedItems = false,
        characteristics = persistentListOf(),
        cleaningServiceTax = null,
        hasCleaningServiceIncluded = false,
        prices = persistentListOf(),
        files = persistentListOf(),
        observations = null,
    )
}
