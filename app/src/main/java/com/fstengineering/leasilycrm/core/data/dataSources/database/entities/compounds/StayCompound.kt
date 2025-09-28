package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
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
        entity = TenantEntity::class,
    )
    val tenant: TenantCompound,
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
    val activeTransfers: List<StayTransferEntity>
        get() = transfers.filter { !it.isDeleted }

    val activeFiles: List<StayFileEntity>
        get() = files.filter { !it.isDeleted }

    fun toDomain(depth: Int = 1): Stay = Stay(
        id = stay.id,
        createDateTime = stay.createDateTime,
        updateDateTime = stay.updateDateTime,
        isFromOtherRealtor = stay.isFromOtherRealtor,
        qtGuests = stay.qtGuests,
        totalPrice = stay.totalPrice,
        startDatetime = stay.startDatetime,
        endDatetime = stay.endDatetime,
        commission = stay.commission,
        observations = stay.observations,
        isFinished = stay.isFinished,
        isDeleted = stay.isDeleted,
        property = if (depth > 0) property.toDomain(depth - 1) else null,
        tenant = tenant.toDomain(),
        transfers = activeTransfers.map { it.toDomain() }.toImmutableList(),
        files = activeFiles.map { it.toDomain() }.toImmutableList(),
        userId = stay.userId,
    )
}
