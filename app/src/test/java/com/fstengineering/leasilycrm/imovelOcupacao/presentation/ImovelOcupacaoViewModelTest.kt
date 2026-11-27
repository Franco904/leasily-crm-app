package com.fstengineering.leasilycrm.imovelOcupacao.presentation

import app.cash.turbine.test
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodosOcupadosReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.queries.ImovelOcupacaoArgs
import com.fstengineering.leasilycrm.imovelOcupacao.application.useCases.WatchImovelOcupacaoUseCase
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelPeriodosOcupadosUiState
import com.fstengineering.leasilycrm.testUtils.annotations.ViewModelCoroutineTest
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.YearMonth

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

    @Nested
    @DisplayName("onYearChange")
    inner class OnYearChangeTests {
        @Test
        fun `Deve exibir só os dias ocupados do ano escolhido quando o ano é trocado`() = runTest {
            val imovelId = faker.random.nextLong(bound = 9999)
            val currentYearMonth = AppYearMonth(YearMonth.of(2026, 3))
            val chosenYearMonth = AppYearMonth(YearMonth.of(2027, 1))
            val chosenDate = createOccupiedDate(LocalDate.of(2027, 1, 5))
            coEvery { watchImovelOcupacaoMock(ImovelOcupacaoArgs(imovelId)) } returns flowOf(
                AppResult.Success(
                    ImovelPeriodosOcupadosReadModel(
                        occupiedPeriods = persistentMapOf(
                            currentYearMonth to persistentListOf(
                                createOccupiedDate(LocalDate.of(2026, 3, 12)),
                            ),
                            chosenYearMonth to persistentListOf(chosenDate),
                        ),
                    ),
                ),
            )
            val sut = createSut(imovelId)
            sut.init()

            sut.onYearChange(2027)

            sut.selectedYear.value shouldBeEqualTo 2027
            val loaded = sut.occupiedPeriods.value
                .shouldBeInstanceOf<ImovelPeriodosOcupadosUiState.Loaded>()
            loaded.selected[chosenYearMonth] shouldBeEqualTo
                persistentListOf(chosenDate.toUiState())
            loaded.selected[currentYearMonth] shouldBeEqualTo persistentListOf()
        }
    }

    private fun createSut(imovelId: Long) = ImovelOcupacaoViewModel(
        route = Destinations.ImovelOcupacao(imovelId = imovelId),
        watchImovelOcupacao = watchImovelOcupacaoMock,
        dateProvider = dateProviderMock,
    )

    private fun createOccupiedDate(date: LocalDate) = ImovelOccupiedDateReadModel(
        date = AppDate(date),
        firstHalfPeriodIndex = null,
        secondHalfPeriodIndex = 0,
        periodId = faker.random.nextLong(bound = 9999),
        tenantName = faker.random.nextUUID(),
    )
}
