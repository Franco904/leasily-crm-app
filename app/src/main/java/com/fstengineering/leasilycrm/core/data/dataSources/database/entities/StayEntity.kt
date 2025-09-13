package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity(
    tableName = "stays",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
        ForeignKey(
            entity = TenantEntity::class,
            parentColumns = ["id"],
            childColumns = ["tenant_id"],
        ),
    ],
    indices = [
        Index("property_id"),
        Index("tenant_id"),
    ],
)
data class StayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "is_from_other_realtor") val isFromOtherRealtor: Boolean,
    @ColumnInfo(name = "qt_guests") val qtGuests: Int,
    @ColumnInfo(name = "total_price") val totalPrice: BigDecimal,
    @ColumnInfo(name = "start_datetime") val startDatetime: LocalDateTime,
    @ColumnInfo(name = "end_datetime") val endDatetime: LocalDateTime,
    val commission: BigDecimal?,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long,
) {
    fun toDomain() = Stay(
        id = id,
        isFromOtherRealtor = isFromOtherRealtor,
        qtGuests = qtGuests,
        totalPrice = totalPrice,
        startDatetime = startDatetime,
        endDatetime = endDatetime,
        commission = commission,
        observations = observations,
        isDeleted = isDeleted,
        property = Property(id = propertyId),
    )
}
