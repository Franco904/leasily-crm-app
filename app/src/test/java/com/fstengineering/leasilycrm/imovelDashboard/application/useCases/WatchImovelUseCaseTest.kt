package com.fstengineering.leasilycrm.imovelDashboard.application.useCases

import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ClienteSharedReadDataSource
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelSummaryReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources.ImovelDashboardReadDataSource
import com.fstengineering.leasilycrm.testUtils.faker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime

class WatchImovelUseCaseTest {
    private val imovelDashboardReadDataSourceMock: ImovelDashboardReadDataSource =
        mockk(relaxUnitFun = true)
    private val clienteSharedReadDataSourceMock: ClienteSharedReadDataSource =
        mockk(relaxUnitFun = true)
    private val dateProviderMock: AppDateProvider = mockk(relaxUnitFun = true)

    private val today = AppDate(LocalDate.of(2026, 3, 10))

    private val imovel = ImovelSummaryReadModel(
        id = faker.random.nextLong(bound = 9999) + 1,
        proprietario = ProprietarioReadModel(
            id = faker.random.nextLong(bound = 9999) + 1,
            name = faker.random.nextUUID(),
        ),
        type = ImovelType.Apartment,
        neighborhood = faker.random.nextUUID(),
        streetAddress = faker.random.nextUUID(),
        number = faker.random.nextInt(bound = 999) + 1,
        mainPictureUriString = faker.random.nextUUID(),
    )

    private val estadiaId = faker.random.nextLong(bound = 9999) + 1
    private val tenantId = faker.random.nextLong(bound = 9999) + 1
    private val tenantName = faker.random.nextUUID()

    @BeforeEach
    fun setUp() {
        every { dateProviderMock.now() } returns today
    }

    @Nested
    @DisplayName("invoke")
    inner class InvokeTests {
        @Test
        fun `Deve compor o painel com o Imóvel, o Proprietário e as datas ocupadas com o nome do Hóspede quando há Estadia no mês corrente`() =
            runTest {
                val currentMonthEstadia =
                    periodoOcupado(id = estadiaId, tenantId = tenantId, checkIn = 12, checkOut = 15)
                every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                    flowOf(AppResult.Success(imovel))
                every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                    flowOf(AppResult.Success(listOf(currentMonthEstadia)))
                coEvery { clienteSharedReadDataSourceMock.getName(tenantId) } returns
                    AppResult.Success(tenantName)
                val sut = createSut()

                val result = sut(ImovelArgs(imovel.id)).first()

                val imovelReadModel = result.shouldBeInstanceOf<AppResult.Success<*>>().data
                    .shouldBeInstanceOf<ImovelReadModel>()
                imovelReadModel shouldBeEqualTo ImovelReadModel(
                    id = imovel.id,
                    mainPictureUriString = imovel.mainPictureUriString,
                    type = imovel.type,
                    neighborhood = imovel.neighborhood,
                    streetAddress = imovel.streetAddress,
                    number = imovel.number,
                    proprietario = imovel.proprietario,
                    currentMonthOccupiedPeriods = imovelReadModel.currentMonthOccupiedPeriods,
                )
                imovelReadModel.currentMonthOccupiedPeriods.first shouldBeEqualTo AppYearMonth.of(
                    2026,
                    3
                )
                imovelReadModel.currentMonthOccupiedPeriods.second
                    .map {
                        Triple(
                            it.date.date.dayOfMonth,
                            it.periodId,
                            it.tenantName
                        )
                    } shouldBeEqualTo
                    (12..15).map { Triple(it, estadiaId, tenantName) }
            }

        @Test
        fun `Deve ignorar as datas ocupadas quando o Período Ocupado é de outro mês`() = runTest {
            val nextMonthEstadia = periodoOcupado(
                id = estadiaId, tenantId = null, checkIn = 20, checkOut = 22, month = 4,
            )
            every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                flowOf(AppResult.Success(imovel))
            every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                flowOf(AppResult.Success(listOf(nextMonthEstadia)))
            val sut = createSut()

            val result = sut(ImovelArgs(imovel.id)).first()

            val imovelReadModel = result.shouldBeInstanceOf<AppResult.Success<*>>().data
                .shouldBeInstanceOf<ImovelReadModel>()
            imovelReadModel.currentMonthOccupiedPeriods.second shouldBeEqualTo
                persistentListOf()
        }

        @Test
        fun `Deve associar cada data ocupada ao nome do seu Hóspede quando há Estadias de Hóspedes diferentes no mês corrente`() =
            runTest {
                val otherTenantId = tenantId + 1
                val otherTenantName = faker.random.nextUUID()
                val firstEstadia = periodoOcupado(
                    id = estadiaId, tenantId = tenantId, checkIn = 12, checkOut = 14,
                )
                val secondEstadia = periodoOcupado(
                    id = estadiaId + 1, tenantId = otherTenantId, checkIn = 20, checkOut = 22,
                )
                every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                    flowOf(AppResult.Success(imovel))
                every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                    flowOf(AppResult.Success(listOf(firstEstadia, secondEstadia)))
                coEvery { clienteSharedReadDataSourceMock.getName(tenantId) } returns
                    AppResult.Success(tenantName)
                coEvery { clienteSharedReadDataSourceMock.getName(otherTenantId) } returns
                    AppResult.Success(otherTenantName)
                val sut = createSut()

                val result = sut(ImovelArgs(imovel.id)).first()

                val imovelReadModel = result.shouldBeInstanceOf<AppResult.Success<*>>().data
                    .shouldBeInstanceOf<ImovelReadModel>()
                imovelReadModel.currentMonthOccupiedPeriods.second
                    .map { it.date.date.dayOfMonth to it.tenantName } shouldBeEqualTo
                    (12..14).map { it to tenantName } + (20..22).map { it to otherTenantName }
            }

        @Test
        fun `Deve compor a data ocupada sem nome de Hóspede quando o Período Ocupado é Indisponível no mês corrente`() =
            runTest {
                val unavailablePeriod = periodoOcupado(
                    id = estadiaId, tenantId = null, checkIn = 5, checkOut = 7,
                )
                every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                    flowOf(AppResult.Success(imovel))
                every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                    flowOf(AppResult.Success(listOf(unavailablePeriod)))
                val sut = createSut()

                val result = sut(ImovelArgs(imovel.id)).first()

                val imovelReadModel = result.shouldBeInstanceOf<AppResult.Success<*>>().data
                    .shouldBeInstanceOf<ImovelReadModel>()
                imovelReadModel.currentMonthOccupiedPeriods.second
                    .map {
                        Triple(
                            it.date.date.dayOfMonth,
                            it.periodId,
                            it.tenantName
                        )
                    } shouldBeEqualTo
                    (5..7).map { Triple(it, estadiaId, null) }
            }

        @Test
        fun `Deve falhar quando a leitura dos Períodos Ocupados falha`() = runTest {
            every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                flowOf(AppResult.Success(imovel))
            every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                flowOf(AppResult.Error(ApplicationError.Unknown))
            val sut = createSut()

            val result = sut(ImovelArgs(imovel.id)).first()

            result.shouldBeInstanceOf<AppResult.Error>().error shouldBeEqualTo
                ApplicationError.Unknown
        }

        @Test
        fun `Deve falhar quando a leitura do Imóvel falha`() = runTest {
            every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                flowOf(AppResult.Error(ApplicationError.Unknown))
            every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                flowOf(AppResult.Success(emptyList()))
            val sut = createSut()

            val result = sut(ImovelArgs(imovel.id)).first()

            result.shouldBeInstanceOf<AppResult.Error>().error shouldBeEqualTo
                ApplicationError.Unknown
        }

        @Test
        fun `Deve falhar quando a leitura do nome do Hóspede falha`() = runTest {
            val estadia =
                periodoOcupado(id = estadiaId, tenantId = tenantId, checkIn = 12, checkOut = 15)
            every { imovelDashboardReadDataSourceMock.watchImovel(imovel.id) } returns
                flowOf(AppResult.Success(imovel))
            every { imovelDashboardReadDataSourceMock.watchPeriodosOcupados(imovel.id) } returns
                flowOf(AppResult.Success(listOf(estadia)))
            coEvery { clienteSharedReadDataSourceMock.getName(tenantId) } returns
                AppResult.Error(ApplicationError.Unknown)
            val sut = createSut()

            val result = sut(ImovelArgs(imovel.id)).first()

            result.shouldBeInstanceOf<AppResult.Error>().error shouldBeEqualTo
                ApplicationError.Unknown
        }
    }

    private fun periodoOcupado(
        id: Long,
        tenantId: Long?,
        checkIn: Int,
        checkOut: Int,
        month: Int = 3,
    ) = ImovelPeriodoOcupado(
        id = id,
        tenantId = tenantId,
        checkInDateTime = AppDateTime(LocalDateTime.of(2026, month, checkIn, 14, 0)),
        checkOutDateTime = AppDateTime(LocalDateTime.of(2026, month, checkOut, 11, 0)),
    )

    private fun createSut() = WatchImovelUseCase(
        imovelDashboardReadDataSource = imovelDashboardReadDataSourceMock,
        clienteSharedReadDataSource = clienteSharedReadDataSourceMock,
        dateProvider = dateProviderMock,
    )
}
