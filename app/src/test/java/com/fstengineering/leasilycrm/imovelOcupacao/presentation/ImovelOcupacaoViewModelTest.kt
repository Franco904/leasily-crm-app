package com.fstengineering.leasilycrm.imovelOcupacao.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.queries.ImovelOcupacaoArgs
import com.fstengineering.leasilycrm.imovelOcupacao.application.useCases.WatchImovelOcupacaoUseCase
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

@ViewModelCoroutineTest
class ImovelOcupacaoViewModelTest {
    private val watchImovelOcupacaoMock: WatchImovelOcupacaoUseCase = mockk(relaxUnitFun = true)
    private val dateProviderMock: AppDateProvider = mockk(relaxUnitFun = true)

    @BeforeEach
    fun setUp() {
        every { dateProviderMock.now() } returns AppDate(LocalDate.of(2026, 3, 10))
    }

    @Nested
    @DisplayName("init")
    inner class InitTests {
        @Test
        fun `Deve informar falha quando a tela é aberta, dado que o armazenamento local falha`() =
            runTest {
                val imovelId = faker.random.nextLong(bound = 9999)
                coEvery { watchImovelOcupacaoMock(ImovelOcupacaoArgs(imovelId)) } returns
                        flowOf(AppResult.Error(ApplicationError.NoInternet))
                val sut = createSut(imovelId)

                sut.uiEvents.test {
                    sut.init()

                    awaitItem() shouldBeEqualTo
                            ImovelOcupacaoViewModel.UiEvent.ShowErrorBottomSheet(ApplicationError.NoInternet)
                }
            }

        @Test
        fun `Deve carregar a ocupação do Imóvel uma única vez quando a tela é aberta mais de uma vez`() =
            runTest {
                val imovelId = faker.random.nextLong(bound = 9999)
                coEvery { watchImovelOcupacaoMock(ImovelOcupacaoArgs(imovelId)) } returns
                        flowOf(AppResult.Error(ApplicationError.NoInternet))
                val sut = createSut(imovelId)

                sut.init()
                sut.init()

                coVerify(exactly = 1) { watchImovelOcupacaoMock(ImovelOcupacaoArgs(imovelId)) }
            }
    }

    private fun createSut(imovelId: Long) = ImovelOcupacaoViewModel(
        route = Destinations.ImovelOcupacao(imovelId = imovelId),
        watchImovelOcupacao = watchImovelOcupacaoMock,
        dateProvider = dateProviderMock,
    )
}
