package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.domain.models.Tenant

data class TenantCompound(
    @Embedded val tenant: TenantEntity,
    @Relation(
        parentColumn = "customer_segment_id",
        entityColumn = "id",
        entity = CustomerSegmentEntity::class,
    )
    val customerSegment: CustomerSegmentCompound?,
    @Relation(
        parentColumn = "tenant_contact_origin_type_id",
        entityColumn = "id",
    )
    val tenantContactOriginType: TenantContactOriginTypeEntity?,
) {
    fun toDomain() = Tenant(
        id = tenant.id,
        registerDate = tenant.registerDate,
        name = tenant.name,
        city = tenant.city,
        identityDocument = tenant.identityDocument,
        phone = tenant.phone,
        email = tenant.email,
        observations = tenant.observations,
        isLead = true,
        isDeleted = tenant.isDeleted,
        customerSegment = customerSegment?.toDomain(),
        tenantContactOriginType = tenantContactOriginType?.toDomain(),
        userId = tenant.userId,
    )
}
