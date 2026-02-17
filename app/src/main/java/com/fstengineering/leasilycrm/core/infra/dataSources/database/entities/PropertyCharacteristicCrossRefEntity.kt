package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.domain.entities.PropertyCharacteristicCrossRef
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "property_characteristics_properties",
    primaryKeys = ["property_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [
        Index("property_id"),
        Index("property_characteristic_id"),
    ],
)
data class PropertyCharacteristicCrossRefEntity(
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "property_characteristic_id") val propertyCharacteristicId: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
) {
    companion object {
        fun fromDomain(domain: PropertyCharacteristicCrossRef, propertyId: Long) =
            PropertyCharacteristicCrossRefEntity(
                propertyId = propertyId,
                propertyCharacteristicId = domain.propertyCharacteristic.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                isDeleted = false,
            )
    }
}
