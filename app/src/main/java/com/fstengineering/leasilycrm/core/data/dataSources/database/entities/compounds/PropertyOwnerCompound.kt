package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds

import androidx.room.Embedded
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
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
    private val activeFiles: List<PropertyOwnerFileEntity>
        get() = files.filter { !it.isDeleted }

    fun toDomain() = PropertyOwner(
        id = propertyOwner.id,
        createDateTime = propertyOwner.createDateTime,
        updateDateTime = propertyOwner.updateDateTime,
        name = propertyOwner.name,
        phone = propertyOwner.phone,
        email = propertyOwner.email,
        paymentKey = propertyOwner.paymentKey,
        isDeleted = propertyOwner.isDeleted,
        files = activeFiles.map { it.toDomain() }.toImmutableList(),
        userId = propertyOwner.userId,
    )
}
