package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.entities.StayFile
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class StayCompound(
    @Embedded val stay: StayEntity,
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
        return if (stay.tenantId != null) {
            Stay.Owned.parse(
                id = stay.id,
                createDateTime = stay.createDateTime,
                updateDateTime = stay.updateDateTime,
                propertyId = stay.propertyId,
                tenantId = stay.tenantId,
                vehicleMake = stay.vehicleMake,
                vehicleColor = stay.vehicleColor,
                vehicleLicensePlate = stay.vehicleLicensePlate,
                guests = stay.guests,
                checkInDateTime = stay.checkInDateTime,
                checkInTime = stay.checkInDateTime.hour to stay.checkInDateTime.minute,
                checkOutDateTime = stay.checkOutDateTime,
                checkOutTime = stay.checkOutDateTime.hour to stay.checkOutDateTime.minute,
                isAutoCheckIn = stay.isAutoCheckIn,
                isAutoCheckOut = stay.isAutoCheckOut,
                statusId = stay.status,
                totalPrice = stay.totalPrice,
                commission = stay.commission,
                observations = stay.observations,
                transfers = activeTransfers.toImmutableList(),
                files = activeFiles.toImmutableList(),
                inconsistencies = persistentListOf(), // stay.inconsistencies,
                contractVersion = stay.contractVersion,
            )
        } else {
            Stay.UnavailablePeriod.parse(
                id = stay.id,
                createDateTime = stay.createDateTime,
                updateDateTime = stay.updateDateTime,
                propertyId = stay.propertyId,
                checkInDateTime = stay.checkInDateTime,
                checkOutDateTime = stay.checkOutDateTime,
                observations = stay.observations,
                inconsistencies = persistentListOf(), // stay.inconsistencies,
                contractVersion = stay.contractVersion,
            )
        }
    }
}
