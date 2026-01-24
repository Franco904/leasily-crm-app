package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.views.TenantReadView
import com.fstengineering.leasilycrm.core.domain.models.Stay
import kotlinx.collections.immutable.toImmutableList

data class StayCompound(
    @Embedded val stay: StayEntity,
    @Relation(
        parentColumn = "property_id",
        entityColumn = "id",
        entity = PropertyEntity::class,
    )
    val property: PropertyCompound,
    @Relation(
        parentColumn = "tenant_id",
        entityColumn = "id",
        entity = TenantReadView::class,
    )
    val tenant: TenantCompound?,
    @Relation(
        parentColumn = "id",
        entityColumn = "stay_id",
        entity = StayTransferEntity::class,
    )
    val transfers: List<StayTransferEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "stay_id",
        entity = StayFileEntity::class,
    )
    val files: List<StayFileEntity>,
) {
    private val activeTransfers: List<StayTransferEntity>
        get() = transfers.filter { !it.isDeleted }

    private val activeFiles: List<StayFileEntity>
        get() = files.filter { !it.isDeleted }

    fun toDomain(): Stay = Stay(
        id = stay.id,
        createDateTime = stay.createDateTime,
        updateDateTime = stay.updateDateTime,
        isUnavailablePeriod = stay.isFromOtherRealtor,
        qtGuests = stay.qtGuests,
        totalPrice = stay.totalPrice,
        checkInDateTime = stay.checkInDateTime,
        checkInHourMinute = stay.checkInDateTime.hour to stay.checkInDateTime.minute,
        checkOutDateTime = stay.checkOutDateTime,
        checkOutHourMinute = stay.checkOutDateTime.hour to stay.checkOutDateTime.minute,
        isAutoCheckIn = stay.isAutoCheckIn,
        isAutoCheckOut = stay.isAutoCheckOut,
        isCheckInDone = stay.isCheckInDone,
        isCheckOutDone = stay.isCheckOutDone,
        commission = stay.commission,
        vehicleMake = stay.vehicleMake,
        vehicleColor = stay.vehicleColor,
        vehicleLicensePlate = stay.vehicleLicensePlate,
        observations = stay.observations,
        isDeleted = stay.isDeleted,
        property = property.toDomain(),
        tenant = tenant?.toDomain(),
        transfers = activeTransfers.map { it.toDomain() }.toImmutableList(),
        files = activeFiles.map { it.toDomain() }.toImmutableList(),
        userId = stay.userId,
    )
}
