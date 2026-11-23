package com.fstengineering.leasilycrm.imovelDetails.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDetails.infra.dataSources.ImovelDetailsReadDataSource
import com.fstengineering.leasilycrm.imovelDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ImovelDetailsViewModelTest {
    private val imovelDetailsReadDataSourceMock: ImovelDetailsReadDataSource = mockk(relaxUnitFun = true)
    private val imovelSharedWriteDataSourceMock: ImovelSharedWriteDataSource = mockk(relaxUnitFun = true)

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir os detalhes do Imóvel quando a tela é aberta`() = runTest {
            val imovel = createImovelReadModel()
            every { imovelDetailsReadDataSourceMock.watchImovel(imovel.id) } returns
                    flowOf(AppResult.Success(imovel))
            val sut = createSut(imovelId = imovel.id)

            sut.imovel.test {
                awaitItem() shouldBeEqualTo ImovelUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo imovel.toUiState()
            }
        }

        @Test
        fun `Deve carregar o Imóvel uma única vez quando a tela é aberta mais de uma vez`() =
            runTest {
                val imovel = createImovelReadModel()
                every { imovelDetailsReadDataSourceMock.watchImovel(imovel.id) } returns
                        flowOf(AppResult.Success(imovel))
                val sut = createSut(imovelId = imovel.id)

                sut.init()
                sut.init()

                verify(exactly = 1) { imovelDetailsReadDataSourceMock.watchImovel(imovel.id) }
            }

        @Test
        fun `Deve informar falha quando a tela é aberta, dado que o armazenamento local falha`() =
            runTest {
                val imovelId = faker.random.nextLong(bound = 9999)
                every { imovelDetailsReadDataSourceMock.watchImovel(imovelId) } returns
                        flowOf(AppResult.Error(ApplicationError.NoInternet))
                val sut = createSut(imovelId = imovelId)

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                            ImovelDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
            }
    }

    @Nested
    @DisplayName("deleteImovel")
    inner class DeleteImovelTests {
        @Test
        fun `Deve confirmar exclusão quando o Imóvel exibido é excluído`() = runTest {
            val imovel = createImovelReadModel()
            every { imovelDetailsReadDataSourceMock.watchImovel(imovel.id) } returns
                    flowOf(AppResult.Success(imovel))
            coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)
            val sut = createSut(imovelId = imovel.id).also { it.init() }

            sut.uiEvents.test {
                sut.deleteImovel()

                awaitItem() shouldBeEqualTo ImovelDetailsViewModel.UiEvent.ShowDeleteSuccessSnackbar
            }
            coVerify { imovelSharedWriteDataSourceMock.delete(imovel.id) }
        }

        @Test
        fun `Deve exibir falha quando o Imóvel exibido é excluído, dado que o armazenamento local falha`() =
            runTest {
                val imovel = createImovelReadModel()
                every { imovelDetailsReadDataSourceMock.watchImovel(imovel.id) } returns
                        flowOf(AppResult.Success(imovel))
                coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.NoInternet)
                val sut = createSut(imovelId = imovel.id).also { it.init() }

                sut.uiEvents.test {
                    sut.deleteImovel()

                    awaitItem() shouldBeEqualTo
                            ImovelDetailsViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que o Imóvel ainda não foi carregado`() =
            runTest {
                val sut = createSut(imovelId = faker.random.nextLong(bound = 9999))

                sut.deleteImovel()

                coVerify(exactly = 0) { imovelSharedWriteDataSourceMock.delete(any()) }
            }
    }

    private fun createSut(imovelId: Long) = ImovelDetailsViewModel(
        route = Destinations.ImovelDetails(imovelId = imovelId),
        imovelDetailsReadDataSource = imovelDetailsReadDataSourceMock,
        imovelSharedWriteDataSource = imovelSharedWriteDataSourceMock,
    )

    private fun createImovelReadModel() = ImovelReadModel(
        id = faker.random.nextLong(bound = 9999),
        type = ImovelType.Apartment,
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
