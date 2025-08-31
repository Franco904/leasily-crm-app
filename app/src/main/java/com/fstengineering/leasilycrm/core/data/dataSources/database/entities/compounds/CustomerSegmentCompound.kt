package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentTypeEntity
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType

data class CustomerSegmentCompound(
    @Embedded val customerSegment: CustomerSegmentEntity,
    @Relation(
        parentColumn = "customer_segment_type_id",
        entityColumn = "id",
    )
    val customerSegmentType: CustomerSegmentTypeEntity,
) {
    fun toDomain() = CustomerSegment(
        id = customerSegment.id,
        name = customerSegment.name,
        isDeleted = customerSegment.isDeleted,
        type = CustomerSegmentType(
            id = customerSegmentType.id,
            name = customerSegmentType.name,
        ),
        userId = customerSegment.userId,
    )
}
