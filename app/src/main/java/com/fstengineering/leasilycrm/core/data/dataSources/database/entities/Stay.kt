package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "stays",
    foreignKeys = [
        ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Tenant::class,
            parentColumns = ["id"],
            childColumns = ["tenant_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("property_id"),
        Index("tenant_id"),
    ],
)
data class Stay(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "is_from_other_realtor") val isFromOtherRealtor: Boolean,
    @ColumnInfo(name = "qt_guests") val qtGuests: Int,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    @ColumnInfo(name = "start_datetime") val startDatetime: LocalDateTime,
    @ColumnInfo(name = "end_datetime") val endDatetime: LocalDateTime,
    val commission: Double?,
    val observations: String?,
    @ColumnInfo(name = "property_id") val propertyId: Int,
    @ColumnInfo(name = "tenant_id") val tenantId: Int,
)
