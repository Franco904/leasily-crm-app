package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.models.StayOwnedTenant
import com.fstengineering.leasilycrm.core.domain.models.StayProperty
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
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
    private val activeTransfers: List<StayTransfer>
        get() = transfers
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    private val activeFiles: List<StayFile>
        get() = files
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    fun toDomain(): Stay {
        return if (tenant != null) {
            Stay.Owned(
                id = requireNotNull(stay.id),
                createDateTime = stay.createDateTime,
                updateDateTime = stay.updateDateTime,
                property = StayProperty.WithData(property.toDomain()),
                tenant = StayOwnedTenant.WithData(tenant.toDomain()),
                vehicleMake = stay.vehicleMake,
                vehicleColor = stay.vehicleColor,
                vehicleLicensePlate = stay.vehicleLicensePlate,
                qtGuests = requireNotNull(stay.qtGuests),
                checkInDateTime = stay.checkInDateTime,
                checkInHourMinute = stay.checkInDateTime.hour to stay.checkInDateTime.minute,
                checkOutDateTime = stay.checkOutDateTime,
                checkOutHourMinute = stay.checkOutDateTime.hour to stay.checkOutDateTime.minute,
                isAutoCheckIn = requireNotNull(stay.isAutoCheckIn),
                isAutoCheckOut = requireNotNull(stay.isAutoCheckOut),
                isCheckInDone = requireNotNull(stay.isCheckInDone),
                isCheckOutDone = requireNotNull(stay.isCheckOutDone),
                totalPrice = stay.totalPrice,
                commission = requireNotNull(stay.commission),
                observations = stay.observations,
                transfers = activeTransfers.toImmutableList(),
                files = activeFiles.toImmutableList(),
                isDeleted = stay.isDeleted,
                userId = stay.userId,
            )
        } else {
            Stay.UnavailablePeriod(
                id = requireNotNull(stay.id),
                createDateTime = stay.createDateTime,
                updateDateTime = stay.updateDateTime,
                property = StayProperty.WithData(property.toDomain()),
                checkInDateTime = stay.checkInDateTime,
                checkOutDateTime = stay.checkOutDateTime,
                observations = stay.observations,
                isDeleted = stay.isDeleted,
                userId = stay.userId,
            )
        }
    }
}
