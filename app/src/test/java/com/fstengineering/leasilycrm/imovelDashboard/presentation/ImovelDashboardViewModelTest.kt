package com.fstengineering.leasilycrm.imovelDashboard.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelDashboard.application.useCases.WatchImovelUseCase
import com.fstengineering.leasilycrm.imovelDashboard.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ImovelDashboardViewModelTest {
    private val watchImovelMock: WatchImovelUseCase = mockk(relaxUnitFun = true)
    private val imovelSharedWriteDataSourceMock: ImovelSharedWriteDataSource =
        mockk(relaxUnitFun = true)

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir o painel do Imóvel quando a tela é aberta`() = runTest {
            val imovel = createImovelReadModel()
            coEvery { watchImovelMock(ImovelArgs(imovel.id)) } returns
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
                coEvery { watchImovelMock(ImovelArgs(imovel.id)) } returns
                    flowOf(AppResult.Success(imovel))
                val sut = createSut(imovelId = imovel.id)

                sut.init()
                sut.init()

                coVerify(exactly = 1) { watchImovelMock(ImovelArgs(imovel.id)) }
            }

        @Test
        fun `Deve informar falha quando a tela é aberta, dado que o armazenamento local falha`() =
            runTest {
                val imovelId = faker.random.nextLong(bound = 9999)
                coEvery { watchImovelMock(ImovelArgs(imovelId)) } returns
                    flowOf(AppResult.Error(ApplicationError.NoInternet))
                val sut = createSut(imovelId = imovelId)

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                        ImovelDashboardViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
            }
    }

    @Nested
    @DisplayName("deleteImovel")
    inner class DeleteImovelTests {
        @Test
        fun `Deve confirmar exclusão quando o Imóvel exibido é excluído`() = runTest {
            val imovel = createImovelReadModel()
            coEvery { watchImovelMock(ImovelArgs(imovel.id)) } returns
                flowOf(AppResult.Success(imovel))
            coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns AppResult.Success(Unit)
            val sut = createSut(imovelId = imovel.id).also { it.init() }

            sut.uiEvents.test {
                sut.deleteImovel()

                awaitItem() shouldBeEqualTo ImovelDashboardViewModel.UiEvent.ShowDeleteSuccessSnackbar
            }
            coVerify { imovelSharedWriteDataSourceMock.delete(imovel.id) }
        }

        @Test
        fun `Deve exibir falha quando o Imóvel exibido é excluído, dado que o armazenamento local falha`() =
            runTest {
                val imovel = createImovelReadModel()
                coEvery { watchImovelMock(ImovelArgs(imovel.id)) } returns
                    flowOf(AppResult.Success(imovel))
                coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns
                    AppResult.Error(ApplicationError.NoInternet)
                val sut = createSut(imovelId = imovel.id).also { it.init() }

                sut.uiEvents.test {
                    sut.deleteImovel()

                    awaitItem() shouldBeEqualTo
                        ImovelDashboardViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
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

    private fun createSut(imovelId: Long) = ImovelDashboardViewModel(
        route = Destinations.ImovelDashboard(imovelId = imovelId),
        watchImovel = watchImovelMock,
        imovelSharedWriteDataSource = imovelSharedWriteDataSourceMock,
    )

    private fun createImovelReadModel() = ImovelReadModel(
        id = faker.random.nextLong(bound = 9999),
        mainPictureUriString = null,
        type = ImovelType.Apartment,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
        number = null,
        proprietario = ProprietarioReadModel(
            id = faker.random.nextLong(bound = 9999),
            name = faker.random.nextUUID(),
        ),
        currentMonthOccupiedPeriods = AppYearMonth.of(2026, 3) to persistentListOf(),
    )
}
