package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import java.time.LocalDate

@Entity(
    tableName = "property_prices",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("property_id")],
)
data class PropertyPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "start_date") val startDate: LocalDate,
    @ColumnInfo(name = "end_date") val endDate: LocalDate,
    val price: Double,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "property_id") val propertyId: Int,
) {
    fun toDomain() = PropertyPrice(
        id = id,
        startDate = startDate,
        endDate = endDate,
        price = price,
        propertyId = propertyId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyPrice): PropertyPriceEntity {
            requireNotNull(domain.startDate) { "PropertyPrice.startDate is required" }
            requireNotNull(domain.endDate) { "PropertyPrice.endDate is required" }
            requireNotNull(domain.price) { "PropertyPrice.price is required" }
            requireNotNull(domain.propertyId) { "PropertyPrice.propertyId is required" }

            return PropertyPriceEntity(
                id = domain.id,
                startDate = domain.startDate,
                endDate = domain.endDate,
                price = domain.price,
                propertyId = domain.propertyId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
