package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class PropertyOwnerCompound(
    @Embedded val propertyOwner: PropertyOwnerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = PropertyOwnerFileEntity::class,
    )
    val files: List<PropertyOwnerFileEntity>,
) {
    private val activeFiles: List<PropertyOwnerFile>
        get() = files
            .filter { !it.isDeleted }
            .map { it.toDomain() }

    fun toDomain() = PropertyOwner.parse(
        id = propertyOwner.id,
        createDateTime = propertyOwner.createDateTime,
        updateDateTime = propertyOwner.updateDateTime,
        name = propertyOwner.name,
        phone = propertyOwner.phone,
        email = propertyOwner.email,
        paymentKey = propertyOwner.paymentKey,
        files = activeFiles.toImmutableList(),
        inconsistencies = persistentListOf(), // propertyOwner.inconsistencies,
        contractVersion = propertyOwner.contractVersion,
    )
}
