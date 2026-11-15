package com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelRawReadModel
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.ImovelFile
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
import com.fstengineering.leasilycrm.testUtils.fakes.createFakeImovel
import com.fstengineering.leasilycrm.testUtils.fakes.saveFakeProprietario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

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
    fun deveObservarImovelComFotoPrincipalEProprietario(): Unit =
        runBlocking(Dispatchers.Default) {
            val proprietarioId = db.saveFakeProprietario()
            val mainPictureUri = faker.random.nextUUID()
            val imovel = createFakeImovel(
                proprietarioId = proprietarioId,
                id = 4L,
                files = listOf(
                    ImovelFile(id = null, uriString = mainPictureUri, isMainPicture = true),
                    ImovelFile(
                        id = null,
                        uriString = faker.random.nextUUID(),
                        isMainPicture = false
                    ),
                ),
            )
            imovelWriteDataSource.save(imovel).shouldBeInstanceOf<AppResult.Success<*, *>>()

            val result = sut.watchImovel(imovelId = 4L).first()

            result.shouldBeInstanceOf<AppResult.Success<ImovelRawReadModel, ApplicationError>>()
                .data shouldBeEqualTo ImovelRawReadModel(
                id = 4L,
                proprietarioId = proprietarioId,
                type = ImovelType.Apartment,
                neighborhood = imovel.neighborhood,
                streetAddress = imovel.streetAddress,
                number = null,
                mainPictureUriString = mainPictureUri,
            )
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

            result.shouldBeInstanceOf<AppResult.Success<List<ImovelPeriodoOcupadoRawReadModel>, ApplicationError>>()
                .data shouldBeEqualTo listOf(earlier, later)
        }

    // endregion

    private suspend fun insertPeriodoOcupado(
        imovelId: Long,
        checkIn: AppDateTime,
        isDeleted: Boolean = false,
    ): ImovelPeriodoOcupadoRawReadModel {
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
        return ImovelPeriodoOcupadoRawReadModel(
            id = id,
            tenantId = null,
            checkInDateTime = checkIn,
            checkOutDateTime = checkOut,
        )
    }
}
