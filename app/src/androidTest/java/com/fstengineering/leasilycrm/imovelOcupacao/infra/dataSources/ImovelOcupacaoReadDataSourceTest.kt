package com.fstengineering.leasilycrm.imovelOcupacao.infra.dataSources

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
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
class ImovelOcupacaoReadDataSourceTest : IntegrationTest() {
    private lateinit var sut: ImovelOcupacaoReadDataSource

    private lateinit var imovelWriteDataSource: ImovelWriteDataSource

    private val now = AppDateTime(LocalDateTime.of(2026, 3, 10, 10, 0))

    @Before
    fun setUp() {
        sut = db.imovelOcupacaoReadDataSource()
        imovelWriteDataSource = db.imovelWriteDataSource()
    }

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

            result.shouldBeInstanceOf<AppResult.Success<List<ImovelPeriodoOcupado>, ApplicationError>>()
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
