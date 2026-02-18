package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.TenantReadView
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class TenantCompound(
    @Embedded val tenant: TenantReadView,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = AssistanceEntity::class,
    )
    val assistances: List<AssistanceEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "tenant_id",
        entity = StayEntity::class,
    )
    val stays: List<StayEntity>,
) {
    private val activeAssistances: List<Long>
        get() = assistances
            .filter { !it.isDeleted }
            .mapNotNull { it.id }

    private val activeStays: List<Long>
        get() = stays
            .filter { !it.isDeleted }
            .mapNotNull { it.id }

    fun toDomain() = Tenant.parse(
        id = tenant.id,
        createDateTime = tenant.createDateTime,
        updateDateTime = tenant.updateDateTime,
        tag = tenant.tag,
        name = tenant.name,
        city = tenant.city,
        identityDocument = tenant.identityDocument,
        phone = tenant.phone,
        email = tenant.email,
        observations = tenant.observations,
        statusId = tenant.status,
        contactOrigin = tenant.contactOrigin,
        assistancesIds = activeAssistances.toImmutableList(),
        staysIds = activeStays.toImmutableList(),
        inconsistencies = persistentListOf(), // tenant.inconsistencies,
        contractVersion = tenant.contractVersion,
    )
}
