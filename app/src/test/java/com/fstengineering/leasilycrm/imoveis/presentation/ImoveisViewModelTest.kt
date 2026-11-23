package com.fstengineering.leasilycrm.imoveis.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imoveis.infra.dataSources.ImoveisReadDataSource
import com.fstengineering.leasilycrm.imoveis.presentation.models.ImoveisUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class ImoveisViewModelTest {
    private val imoveisReadDataSourceMock: ImoveisReadDataSource = mockk(relaxed = true)
    private val imovelSharedWriteDataSourceMock: ImovelSharedWriteDataSource = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        coEvery { imoveisReadDataSourceMock.getImoveis(any()) } returns AppResult.Success(emptyList())
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Imóveis quando a listagem é aberta`() = runTest {
            val sut = createSut()
            val imovel1 = createImovelReadModel()
            val imovel2 = createImovelReadModel()
            every { imoveisReadDataSourceMock.watchImoveis() } returns
                    flowOf(AppResult.Success(listOf(imovel1, imovel2)))

            sut.uiState.test {
                awaitItem() shouldBeEqualTo ImoveisUiState.Loading

                sut.init()

                awaitItem() shouldBeEqualTo ImoveisUiState.Loaded(
                    imoveis = persistentListOf(imovel1.toCardUiState(), imovel2.toCardUiState()),
                )
            }
        }

        @Test
        fun `Deve carregar Imóveis uma única vez quando a tela é aberta mais de uma vez`() =
            runTest {
                val sut = createSut()
                every { imoveisReadDataSourceMock.watchImoveis() } returns
                        flowOf(AppResult.Success(listOf(createImovelReadModel())))

                sut.init()
                sut.init()

                verify(exactly = 1) { imoveisReadDataSourceMock.watchImoveis() }
            }

        @Test
        fun `Deve informar falha quando a listagem é aberta, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSut()
                every { imoveisReadDataSourceMock.watchImoveis() } returns
                        flowOf(AppResult.Error(ApplicationError.NoInternet))

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                            ImoveisViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
            }
    }

    @Nested
    @DisplayName("onSearchInputChange")
    inner class OnSearchInputChangeTests {
        @Test
        fun `Deve exibir apenas Imóveis que correspondem ao texto quando a busca é digitada`() =
            runTest {
                val sut = createSut()
                val matchingImovel = createImovelReadModel()
                every { imoveisReadDataSourceMock.watchImoveis() } returns emptyFlow()
                coEvery { imoveisReadDataSourceMock.getImoveis("cen") } returns
                        AppResult.Success(listOf(matchingImovel))

                sut.uiState.test {
                    awaitItem() shouldBeEqualTo ImoveisUiState.Loading

                    sut.init()
                    sut.onSearchInputChange("cen")
                    advanceTimeBy(600)

                    awaitItem() shouldBeEqualTo
                            ImoveisUiState.Loaded(imoveis = persistentListOf(matchingImovel.toCardUiState()))
                }
            }
    }

    @Nested
    @DisplayName("deleteImovel")
    inner class DeleteImovelTests {
        @Test
        fun `Deve confirmar exclusão e descartar a seleção quando Imóvel selecionado é excluído`() =
            runTest {
                val sut = createSut()
                val imovel = createImovelCardUiState()
                sut.onDeleteImovel(imovel)
                coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns AppResult.Success(
                    Unit
                )

                sut.uiEvents.test {
                    sut.deleteImovel()

                    awaitItem() shouldBeEqualTo ImoveisViewModel.UiEvent.ShowDeleteSuccessSnackbar
                }
                sut.imovelToDelete.value.shouldBeNull()
                coVerify { imovelSharedWriteDataSourceMock.delete(imovel.id) }
            }

        @Test
        fun `Deve exibir falha e manter a seleção quando Imóvel selecionado é excluído, dado que o armazenamento local falha`() =
            runTest {
                val sut = createSut()
                val imovel = createImovelCardUiState()
                sut.onDeleteImovel(imovel)
                coEvery { imovelSharedWriteDataSourceMock.delete(any()) } returns
                        AppResult.Error(ApplicationError.NoInternet)

                sut.uiEvents.test {
                    sut.deleteImovel()

                    awaitItem() shouldBeEqualTo
                            ImoveisViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
                sut.imovelToDelete.value shouldBeEqualTo imovel
            }

        @Test
        fun `Deve ignorar exclusão quando solicitada, dado que nenhum Imóvel está selecionado`() =
            runTest {
                val sut = createSut()
                sut.onDeleteImovel(null)

                sut.deleteImovel()

                coVerify(exactly = 0) { imovelSharedWriteDataSourceMock.delete(any()) }
            }
    }

    private fun createSut() = ImoveisViewModel(
        imoveisReadDataSource = imoveisReadDataSourceMock,
        imovelSharedWriteDataSource = imovelSharedWriteDataSourceMock,
    )

    private fun createImovelReadModel() = ImovelReadModel(
        id = faker.random.nextLong(bound = 9999),
        mainPictureUriString = null,
        type = ImovelType.Apartment,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
        number = faker.random.nextInt(bound = 999),
    )

    private fun ImovelReadModel.toCardUiState() = ImovelCardUiState(
        id = id,
        mainPictureUriString = null,
        neighborhood = neighborhood,
        streetAddressAndNumber = "$streetAddress, $number",
        type = type,
    )

    private fun createImovelCardUiState() = createImovelReadModel().toCardUiState()
}
