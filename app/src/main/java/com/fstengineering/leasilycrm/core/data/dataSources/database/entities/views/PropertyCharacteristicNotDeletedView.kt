package com.fstengineering.leasilycrm.core.data.dataSources.database.entities.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic

@DatabaseView(
    viewName = "property_characteristics_not_deleted_view",
    value = """
        SELECT *
        FROM property_characteristics pc
        INNER JOIN property_characteristics_properties pcp
        ON pcp.property_characteristic_id = pc.id
        WHERE pcp.is_deleted = 0
    """
)
data class PropertyCharacteristicNotDeletedView(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean
) {
    fun toDomain() = PropertyCharacteristic(
        id = id,
        name = name,
    )
}
