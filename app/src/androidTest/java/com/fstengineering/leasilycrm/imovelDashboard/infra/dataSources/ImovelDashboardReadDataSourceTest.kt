package com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelSummaryReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovelFile
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ImovelDashboardReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelDashboardReadDataSource

    private lateinit var imovelWriteDataSource: ImovelWriteDataSource

    private val now = AppDateTime(LocalDateTime.of(2026, 3, 10, 10, 0))

    @Before
    fun setUp() {
        sut = db.imovelDashboardReadDataSource()
        imovelWriteDataSource = db.imovelWriteDataSource()
    }

    // region watchImovel

    @Test
    fun deveObservarImovelComFotoPrincipalENomeDoProprietario(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario(name = "Carla")
            val mainPictureUri = faker.random.nextUUID()
            val imovel = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 4L,
                files = listOf(
                    createFakeImovelFile(id = null, uriString = mainPictureUri, isMainPicture = true),
                    createFakeImovelFile(
                        id = null,
                        uriString = faker.random.nextUUID(),
                        isMainPicture = false
                    ),
                ),
            )
            imovelWriteDataSource.save(imovel).shouldBeInstanceOf<AppResult.Success<*>>()

            val result = sut.watchImovel(imovelId = 4L).first()

            result.shouldBeInstanceOf<AppResult.Success<ImovelSummaryReadModel>>()
                .data shouldBeEqualTo ImovelSummaryReadModel(
                id = 4L,
                proprietario = ProprietarioReadModel(id = proprietarioId, name = "Carla"),
                type = ImovelType.Apartment,
                neighborhood = imovel.neighborhood,
                streetAddress = imovel.streetAddress,
                number = null,
                mainPictureUriString = mainPictureUri,
            )
        }

    @Test
    fun deveReemitirImovelQuandoNomeDoProprietarioMuda(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario(name = "Carla")
            imovelWriteDataSource.save(
                createFakeImovel(proprietarioId = proprietarioId, id = 4L)
            ).shouldBeInstanceOf<AppResult.Success<*>>()
            val emittedNames = mutableListOf<String?>()

            sut.watchImovel(imovelId = 4L)
                .take(2)
                .onEach { result ->
                    emittedNames += result.shouldBeInstanceOf<AppResult.Success<ImovelSummaryReadModel>>()
                        .data.proprietario?.name

                    if (emittedNames.size == 1) {
                        db.saveFakeProprietario(name = "Ana", proprietarioId = proprietarioId)
                    }
                }
                .toList()

            emittedNames shouldBeEqualTo listOf("Carla", "Ana")
        }

    @Test
    fun deveObservarImovelSemProprietarioQuandoProprietarioExcluido(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            imovelWriteDataSource.save(
                createFakeImovel(proprietarioId = proprietarioId, id = 4L)
            ).shouldBeInstanceOf<AppResult.Success<*>>()
            db.proprietarioWriteDataSource().delete(proprietarioId)
                .shouldBeInstanceOf<AppResult.Success<*>>()

            val result = sut.watchImovel(imovelId = 4L).first()

            result.shouldBeInstanceOf<AppResult.Success<ImovelSummaryReadModel>>()
                .data.proprietario.shouldBeNull()
        }

    // endregion

    // region watchPeriodosOcupados

    @Test
    fun deveObservarPeriodosOcupadosDoImovelEmOrdemDeCheckInSemOsExcluidos(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            imovelWriteDataSource.save(createFakeImovel(proprietarioId = proprietarioId, id = 1L))
            imovelWriteDataSource.save(createFakeImovel(proprietarioId = proprietarioId, id = 2L))
            val later = insertPeriodoOcupado(imovelId = 1L, checkIn = now.plusDays(10))
            val earlier = insertPeriodoOcupado(imovelId = 1L, checkIn = now.plusDays(2))
            insertPeriodoOcupado(imovelId = 1L, checkIn = now.plusDays(5), isDeleted = true)
            insertPeriodoOcupado(imovelId = 2L, checkIn = now.plusDays(3))

            val result = sut.watchPeriodosOcupados(imovelId = 1L).first()

            result.shouldBeInstanceOf<AppResult.Success<List<ImovelPeriodoOcupado>>>()
                .data shouldBeEqualTo listOf(earlier, later)
        }

    // endregion

    private suspend fun insertPeriodoOcupado(
        imovelId: Long,
        checkIn: AppDateTime,
        isDeleted: Boolean = false,
    ): ImovelPeriodoOcupado {
        val checkOut = checkIn.plusDays(3)
        val id = db.stayDao().insertOrUpdate(
            StayEntity(
                id = null,
                createDateTime = now,
                updateDateTime = now,
                vehicleMake = null,
                vehicleColor = null,
                vehicleLicensePlate = null,
                guests = null,
                checkInDateTime = checkIn,
                checkOutDateTime = checkOut,
                isAutoCheckIn = null,
                isAutoCheckOut = null,
                totalPrice = null,
                commission = null,
                observations = null,
                isDeleted = isDeleted,
                status = null,
                imovelId = imovelId,
                tenantId = null,
                contractVersion = 1,
            )
        )
        return ImovelPeriodoOcupado(
            id = id,
            tenantId = null,
            checkInDateTime = checkIn,
            checkOutDateTime = checkOut,
        )
    }
}
