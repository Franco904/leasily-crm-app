package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import kotlinx.collections.immutable.persistentListOf
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
    @ColumnInfo(name = "qt_guests") val qtGuests: Int? = null,
    @ColumnInfo(name = "total_price") val totalPrice: BigDecimal? = null,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: LocalDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: LocalDateTime,
    @ColumnInfo(name = "is_auto_check_in") val isAutoCheckIn: Boolean = false,
    @ColumnInfo(name = "is_auto_check_out") val isAutoCheckOut: Boolean = false,
    @ColumnInfo(name = "is_check_in_done") val isCheckInDone: Boolean = false,
    @ColumnInfo(name = "is_check_out_done") val isCheckOutDone: Boolean = false,
    val commission: BigDecimal? = null,
    @ColumnInfo(name = "vehicle_make") val vehicleMake: String? = null,
    @ColumnInfo(name = "vehicle_color") val vehicleColor: String? = null,
    @ColumnInfo(name = "vehicle_license_plate") val vehicleLicensePlate: String? = null,
    val observations: String? = null,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long? = null,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    fun toDomain() = Stay(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        isFromOtherRealtor = isFromOtherRealtor,
        qtGuests = qtGuests,
        totalPrice = totalPrice,
        checkInDateTime = checkInDateTime,
        checkInHourMinute = checkInDateTime.hour to checkInDateTime.minute,
        checkOutDateTime = checkOutDateTime,
        checkOutHourMinute = checkOutDateTime.hour to checkOutDateTime.minute,
        isAutoCheckIn = isAutoCheckIn,
        isAutoCheckOut = isAutoCheckOut,
        isCheckInDone = isCheckInDone,
        isCheckOutDone = isCheckOutDone,
        commission = commission,
        vehicleMake = vehicleMake,
        vehicleColor = vehicleColor,
        vehicleLicensePlate = vehicleLicensePlate,
        observations = observations,
        isDeleted = isDeleted,
        property = Property(id = propertyId),
        tenant = Tenant(id = tenantId),
        transfers = persistentListOf(),
        files = persistentListOf(),
        userId = userId,
    )

    companion object {
        fun fromDomain(domain: Stay): StayEntity {
            requireNotNull(domain.checkInDateTime) { "Stay.checkInDateTime is required" }
            requireNotNull(domain.checkOutDateTime) { "Stay.checkOutDateTime is required" }
            requireNotNull(domain.property?.id) { "Stay.property.id is required" }
            requireNotNull(domain.userId) { "Stay.userId is required" }

            val (checkInHour, checkInMinute) = domain.checkInHourMinute
            val (checkOutHour, checkOutMinute) = domain.checkOutHourMinute

            val adjustedCheckInDateTime = domain.checkInDateTime
                .withHour(checkInHour ?: 0)
                .withMinute(checkInMinute ?: 0)

            val adjustedCheckOutDateTime = domain.checkOutDateTime
                .withHour(checkOutHour ?: 23)
                .withMinute(checkOutMinute ?: 59)

            if (domain.isFromOtherRealtor) {
                return StayEntity(
                    id = domain.id,
                    createDateTime = domain.createDateTime ?: LocalDateTime.now(),
                    updateDateTime = LocalDateTime.now(),
                    isFromOtherRealtor = true,
                    checkInDateTime = adjustedCheckInDateTime,
                    checkOutDateTime = adjustedCheckOutDateTime,
                    propertyId = domain.property.id,
                    userId = domain.userId,
                )
            }

            requireNotNull(domain.qtGuests) { "Stay.qtGuests is required" }
            requireNotNull(checkInHour) { "checkInHour is required" }
            requireNotNull(checkInMinute) { "checkInMinute is required" }
            requireNotNull(checkOutHour) { "checkOutHour is required" }
            requireNotNull(checkOutMinute) { "checkOutMinute is required" }
            requireNotNull(domain.totalPrice) { "Stay.totalPrice is required" }
            requireNotNull(domain.tenant?.id) { "Stay.tenant.id is required" }

            return StayEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: LocalDateTime.now(),
                updateDateTime = LocalDateTime.now(),
                isFromOtherRealtor = false,
                vehicleMake = domain.vehicleMake,
                vehicleColor = domain.vehicleColor,
                vehicleLicensePlate = domain.vehicleLicensePlate,
                qtGuests = domain.qtGuests,
                checkInDateTime = adjustedCheckInDateTime,
                checkOutDateTime = adjustedCheckOutDateTime,
                isAutoCheckIn = domain.isAutoCheckIn,
                isAutoCheckOut = domain.isAutoCheckOut,
                totalPrice = domain.totalPrice,
                commission = domain.commission,
                observations = domain.observations,
                isCheckInDone = domain.isCheckInDone,
                isCheckOutDone = domain.isCheckOutDone,
                isDeleted = domain.isDeleted,
                propertyId = domain.property.id,
                tenantId = domain.tenant.id,
                userId = domain.userId,
            )
        }
    }
}
