package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristicProperty

@Entity(
    tableName = "property_characteristics_properties",
    primaryKeys = ["property_id", "property_characteristic_id"],
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
        ForeignKey(
            entity = PropertyCharacteristicEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_characteristic_id"],
        )
    ],
    indices = [
        Index("property_id"),
        Index("property_characteristic_id"),
    ],
)
data class PropertyCharacteristicPropertyCrossRefEntity(
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "property_characteristic_id") val propertyCharacteristicId: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
) {
    companion object {
        fun fromDomain(domain: PropertyCharacteristicProperty): PropertyCharacteristicPropertyCrossRefEntity {
            requireNotNull(domain.propertyId) { "PropertyCharacteristicProperty.propertyId is required" }
            requireNotNull(domain.propertyCharacteristic?.id) { "PropertyCharacteristicProperty.propertyCharacteristic.id is required" }

            return PropertyCharacteristicPropertyCrossRefEntity(
                propertyId = domain.propertyId,
                propertyCharacteristicId = domain.propertyCharacteristic.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                isDeleted = domain.isDeleted,
            )
        }
    }
}
