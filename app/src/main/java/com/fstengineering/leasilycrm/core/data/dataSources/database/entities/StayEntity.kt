package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
        Index("user_id"),
    ],
)
data class StayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: LocalDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: LocalDateTime,
    @ColumnInfo(name = "is_from_other_realtor") val isFromOtherRealtor: Boolean,
    @ColumnInfo(name = "qt_guests") val qtGuests: Int,
    @ColumnInfo(name = "total_price") val totalPrice: BigDecimal,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: LocalDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: LocalDateTime,
    val commission: BigDecimal?,
    val observations: String?,
    @ColumnInfo(name = "is_finished") val isFinished: Boolean = false,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    companion object {
        fun fromDomain(domain: Stay): StayEntity {
            val (checkInHour, checkInMinute) = domain.checkInHourMinute
            val (checkOutHour, checkOutMinute) = domain.checkOutHourMinute

            requireNotNull(domain.totalPrice) { "Stay.totalPrice is required" }
            requireNotNull(domain.checkInDateTime) { "Stay.checkInDateTime is required" }
            requireNotNull(checkInHour) { "checkInHour is required" }
            requireNotNull(checkInMinute) { "checkInMinute is required" }
            requireNotNull(domain.checkOutDateTime) { "Stay.checkOutDateTime is required" }
            requireNotNull(checkOutHour) { "checkOutHour is required" }
            requireNotNull(checkOutMinute) { "checkOutMinute is required" }
            requireNotNull(domain.property?.id) { "Stay.property.id is required" }
            requireNotNull(domain.tenant?.id) { "Stay.tenant.id is required" }
            requireNotNull(domain.userId) { "Stay.userId is required" }

            val adjustedCheckInDateTime = domain.checkInDateTime
                .withHour(checkInHour)
                .withMinute(checkInMinute)

            val adjustedCheckOutDateTime = domain.checkOutDateTime
                .withHour(checkOutHour)
                .withMinute(checkOutMinute)

            return StayEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: LocalDateTime.now(),
                updateDateTime = LocalDateTime.now(),
                isFromOtherRealtor = domain.isFromOtherRealtor,
                qtGuests = domain.qtGuests,
                totalPrice = domain.totalPrice,
                checkInDateTime = adjustedCheckInDateTime,
                checkOutDateTime = adjustedCheckOutDateTime,
                commission = domain.commission,
                observations = domain.observations,
                isFinished = domain.isFinished,
                isDeleted = domain.isDeleted,
                propertyId = domain.property.id,
                tenantId = domain.tenant.id,
                userId = domain.userId,
            )
        }
    }
}
