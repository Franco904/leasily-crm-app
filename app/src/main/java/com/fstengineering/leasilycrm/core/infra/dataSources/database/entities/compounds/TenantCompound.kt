package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantContactOriginTypeEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
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
    private val activeAssistances: List<Assistance>
        get() = assistances
            .filter { !it.assistance.isDeleted }
            .map { it.toDomain() }

    private val activeStays: List<Stay>
        get() = stays
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    fun toDomain() = Tenant(
        id = requireNotNull(tenant.id),
        createDateTime = tenant.createDateTime,
        updateDateTime = tenant.updateDateTime,
        tag = tenant.tag,
        name = tenant.name,
        city = tenant.city,
        identityDocument = tenant.identityDocument,
        phone = tenant.phone,
        email = tenant.email,
        observations = tenant.observations,
        status = TenantStatus.fromId(id = tenant.status),
        isDeleted = tenant.isDeleted,
        tenantContactOriginType = tenantContactOriginType?.toDomain(),
        assistances = activeAssistances.toImmutableList(),
        stays = activeStays.toImmutableList(),
        userId = tenant.userId,
    )
}
