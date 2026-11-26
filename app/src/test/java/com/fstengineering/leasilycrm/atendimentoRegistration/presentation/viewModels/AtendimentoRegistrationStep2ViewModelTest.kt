package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels

import app.cash.turbine.test
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoSelectableImovelReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.ImovelReadDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ViewModelCoroutineTest
class AtendimentoRegistrationStep2ViewModelTest {
    private val imovelReadDataSourceMock: ImovelReadDataSource = mockk(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        coEvery { imovelReadDataSourceMock.getImoveis(any()) } returns AppResult.Success(emptyList())
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve exibir Imóveis selecionáveis quando a etapa é aberta`() = runTest {
            coEvery { imovelReadDataSourceMock.getImoveis(null) } returns
                AppResult.Success(listOf(createImovel(id = 1L), createImovel(id = 2L)))
            val sut = createSut()

            sut.imoveis.test {
                awaitItem() shouldBeEqualTo emptyList()

                sut.init()

                awaitItem().map { it.id } shouldBeEqualTo listOf(1L, 2L)
            }
        }

        @Test
        fun `Deve informar falha quando a etapa é aberta, dado que o armazenamento local falha`() =
            runTest {
                coEvery { imovelReadDataSourceMock.getImoveis(any()) } returns
                    AppResult.Error(ApplicationError.Unknown)
                val sut = createSut()

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                        AtendimentoRegistrationStep2ViewModel.UiEvent.ShowErrorBottomSheet(
                            ApplicationError.Unknown,
                        )
                }
            }

        @Test
        fun `Deve exibir Imóveis sem esperar a digitação quando a etapa é aberta`() = runTest {
            val sut = createSut()

            sut.init()
            advanceTimeBy(600)

            coVerify(exactly = 1) { imovelReadDataSourceMock.getImoveis(null) }
        }
    }

    @Nested
    @DisplayName("onSearchInputChange")
    inner class OnSearchInputChangeTests {
        @Test
        fun `Deve buscar Imóveis pelo texto digitado quando a digitação pausa`() = runTest {
            coEvery { imovelReadDataSourceMock.getImoveis("Campeche") } returns
                AppResult.Success(listOf(createImovel(id = 9L)))
            val sut = createSut()
            sut.init()

            sut.imoveis.test {
                awaitItem() shouldBeEqualTo emptyList()

                sut.onSearchInputChange("Campeche")
                advanceTimeBy(600)

                awaitItem().map { it.id } shouldBeEqualTo listOf(9L)
            }
        }

        @Test
        fun `Deve listar todos os Imóveis quando o texto digitado é apagado`() = runTest {
            val sut = createSut()
            sut.init()
            sut.onSearchInputChange("Campeche")
            advanceTimeBy(600)

            sut.onSearchInputChange("   ")
            advanceTimeBy(600)

            coVerify(exactly = 2) { imovelReadDataSourceMock.getImoveis(null) }
        }
    }

    private fun createSut() = AtendimentoRegistrationStep2ViewModel(
        imovelReadDataSource = imovelReadDataSourceMock,
    )

    private fun createImovel(id: Long) = AtendimentoSelectableImovelReadModel(
        id = id,
        type = ImovelType.Apartment,
        neighborhood = "Centro",
        streetAddress = "Rua A",
        number = null,
        seaProximityTypeId = null,
        distanceToSea = null,
        bedroomsCount = 2,
        mainPictureUriString = null,
    )
}
