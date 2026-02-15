package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice

@Entity(
    tableName = "property_prices",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
    ],
    indices = [Index("property_id")],
)
data class PropertyPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "start_date") val startDate: AppDate,
    @ColumnInfo(name = "end_date") val endDate: AppDate,
    val price: AppDecimal,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_id") val propertyId: Long,
) {
    fun toDomain() = PropertyPrice(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        startDate = startDate,
        endDate = endDate,
        price = price,
        propertyId = propertyId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: PropertyPrice) =
            PropertyPriceEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                startDate = domain.startDate,
                endDate = domain.endDate,
                price = domain.price,
                propertyId = domain.propertyId,
                isDeleted = domain.isDeleted,
            )
    }
}
