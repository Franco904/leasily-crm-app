package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.views.TenantReadView
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import kotlinx.collections.immutable.toImmutableList

data class TenantCompound(
    @Embedded val tenant: TenantReadView,
    @Relation(
        parentColumn = "tenant_contact_origin_type_id",
        entityColumn = "id",
    )
    val tenantContactOriginType: TenantContactOriginTypeEntity?,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = AssistanceEntity::class,
    )
    val assistances: List<AssistanceCompound>,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = StayEntity::class,
    )
    val stays: List<StayEntity>,
) {
    val activeAssistances: List<AssistanceCompound>
        get() = assistances.filter { !it.assistance.isDeleted }

    val activeStays: List<StayEntity>
        get() = stays.filter { !it.isDeleted }

    fun toDomain() = Tenant(
        id = tenant.id,
        createDateTime = tenant.createDateTime,
        updateDateTime = tenant.updateDateTime,
        name = tenant.name,
        city = tenant.city,
        identityDocument = tenant.identityDocument,
        phone = tenant.phone,
        email = tenant.email,
        observations = tenant.observations,
        status = TenantStatus.fromId(id = tenant.status),
        isDeleted = tenant.isDeleted,
        tenantContactOriginType = tenantContactOriginType?.toDomain(),
        assistances = activeAssistances.map { it.toDomain() }.toImmutableList(),
        stays = activeStays.map { it.toDomain() }.toImmutableList(),
        userId = tenant.userId,
    )
}
