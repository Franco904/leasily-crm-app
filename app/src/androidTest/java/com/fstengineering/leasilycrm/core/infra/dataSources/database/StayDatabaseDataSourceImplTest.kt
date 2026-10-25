package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckInCheckOutCountsProjection
import com.fstengineering.leasilycrm.testUtils.IntegrationTest
import com.fstengineering.leasilycrm.testUtils.faker
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
class StayDatabaseDataSourceImplTest : IntegrationTest() {
    private lateinit var sut: StayDatabaseDataSourceImpl

    private val today = AppDateTime(LocalDateTime.of(2026, 3, 10, 10, 0))

    @Before
    fun setUp() {
        appDateTimeProvider.currentDateTime = today
        sut = StayDatabaseDataSourceImpl(dao = db.stayDao(), dateTimeProvider = appDateTimeProvider)
    }

    // region watchAllPastCheckInCheckOutNotDoneCounts

    @Test
    fun deveContarEstadiasAguardandoCheckInQuandoCheckInEHojeOuJaPassou(): Unit =
        runBlocking(Dispatchers.Default) {
            val imovelId = insertImovel()
            insertEstadia(imovelId, StayStatus.Upcoming, checkIn = today.minusDays(1))
            insertEstadia(imovelId, StayStatus.Upcoming, checkIn = today.withHourMinute(22, 0))
            insertEstadia(imovelId, StayStatus.Upcoming, checkIn = today.plusDays(1))

            val result = sut.watchAllPastCheckInCheckOutNotDoneCounts().first()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>().data shouldBeEqualTo
                StayCheckInCheckOutCountsProjection(
                    staysAbleToCheckInCount = 2,
                    staysAbleToCheckOutCount = 0,
                )
        }

    @Test
    fun deveContarEstadiasEmAndamentoAptasACheckOutQuandoCheckOutEHojeOuJaPassou(): Unit =
        runBlocking(Dispatchers.Default) {
            val imovelId = insertImovel()
            insertEstadia(
                imovelId,
                StayStatus.CheckInDone,
                checkIn = today.minusDays(5),
                checkOut = today.withHourMinute(11, 0),
            )
            insertEstadia(
                imovelId,
                StayStatus.CheckInDone,
                checkIn = today.minusDays(5),
                checkOut = today.plusDays(2),
            )
            insertEstadia(
                imovelId,
                StayStatus.CheckOutDone,
                checkIn = today.minusDays(5),
                checkOut = today.minusDays(1),
            )

            val result = sut.watchAllPastCheckInCheckOutNotDoneCounts().first()

            result.shouldBeInstanceOf<AppResult.Success<*, *>>().data shouldBeEqualTo
                StayCheckInCheckOutCountsProjection(
                    staysAbleToCheckInCount = 0,
                    staysAbleToCheckOutCount = 1,
                )
        }

    // endregion

    private suspend fun insertImovel(): Long {
        val proprietarioId = db.proprietarioDao().insertOrUpdate(
            ProprietarioOrm(
                id = null,
                createDateTime = today,
                updateDateTime = today,
                name = faker.random.nextUUID(),
                phone = null,
                email = null,
                paymentKey = null,
                isDeleted = false,
                contractVersion = 1,
            )
        )

        return db.propertyDao().insertOrUpdate(
            PropertyEntity(
                id = null,
                createDateTime = today,
                updateDateTime = today,
                neighborhood = faker.random.nextUUID(),
                streetAddress = faker.random.nextUUID(),
                cepPostalCode = null,
                number = null,
                addressLine2 = null,
                parkingSpotNumber = null,
                parkingBasementLevelNumber = null,
                hobbyBoxNumber = null,
                bedrooms = 1,
                enSuites = 0,
                doubleBeds = 1,
                singleBeds = 0,
                bathrooms = 1,
                parkingSpots = 0,
                airConditioners = 0,
                maxGuests = 2,
                distanceToSea = null,
                includedItems = null,
                hasIncludedItems = false,
                cleaningServiceTax = null,
                hasCleaningServiceIncluded = false,
                observations = null,
                isDeleted = false,
                ownerId = proprietarioId,
                typeId = 1,
                seaProximityTypeId = null,
                contractVersion = 1,
            )
        )
    }

    private suspend fun insertEstadia(
        imovelId: Long,
        status: StayStatus,
        checkIn: AppDateTime,
        checkOut: AppDateTime = checkIn.plusDays(3),
    ) {
        sut.insertOrUpdate(
            StayEntity(
                id = null,
                createDateTime = today,
                updateDateTime = today,
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
                isDeleted = false,
                status = status.id,
                propertyId = imovelId,
                tenantId = null,
                contractVersion = 1,
            )
        )
    }
}
