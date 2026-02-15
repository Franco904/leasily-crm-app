package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayOwnedTenant
import com.fstengineering.leasilycrm.core.domain.models.StayProperty
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import kotlinx.collections.immutable.persistentListOf

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
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "vehicle_make") val vehicleMake: String?,
    @ColumnInfo(name = "vehicle_color") val vehicleColor: String?,
    @ColumnInfo(name = "vehicle_license_plate") val vehicleLicensePlate: String?,
    @ColumnInfo(name = "qt_guests") val qtGuests: Int?,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: AppDateTime,
    @ColumnInfo(name = "is_auto_check_in") val isAutoCheckIn: Boolean?,
    @ColumnInfo(name = "is_auto_check_out") val isAutoCheckOut: Boolean?,
    @ColumnInfo(name = "is_check_in_done") val isCheckInDone: Boolean?,
    @ColumnInfo(name = "is_check_out_done") val isCheckOutDone: Boolean?,
    @ColumnInfo(name = "total_price") val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long?,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    fun toDomain(): Stay {
        return if (tenantId != null) {
            Stay.Owned(
                id = requireNotNull(id),
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                property = StayProperty.WithId(propertyId),
                tenant = StayOwnedTenant.WithId(tenantId),
                vehicleMake = vehicleMake,
                vehicleColor = vehicleColor,
                vehicleLicensePlate = vehicleLicensePlate,
                qtGuests = requireNotNull(qtGuests),
                checkInDateTime = checkInDateTime,
                checkInHourMinute = checkInDateTime.hour to checkInDateTime.minute,
                checkOutDateTime = checkOutDateTime,
                checkOutHourMinute = checkOutDateTime.hour to checkOutDateTime.minute,
                isAutoCheckIn = requireNotNull(isAutoCheckIn),
                isAutoCheckOut = requireNotNull(isAutoCheckOut),
                isCheckInDone = requireNotNull(isCheckInDone),
                isCheckOutDone = requireNotNull(isCheckOutDone),
                totalPrice = totalPrice,
                commission = requireNotNull(commission),
                observations = observations,
                transfers = persistentListOf(),
                files = persistentListOf(),
                isDeleted = isDeleted,
                userId = userId,
            )
        } else {
            Stay.UnavailablePeriod(
                id = requireNotNull(id),
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                property = StayProperty.WithId(propertyId),
                checkInDateTime = checkInDateTime,
                checkOutDateTime = checkOutDateTime,
                observations = observations,
                isDeleted = isDeleted,
                userId = userId,
            )
        }
    }

    companion object {
        fun fromDomain(domain: Stay): StayEntity {
            val (checkInHour, checkInMinute) = if (domain is Stay.Owned) domain.checkInHourMinute else (0 to 0)
            val (checkOutHour, checkOutMinute) = if (domain is Stay.Owned) domain.checkOutHourMinute else (23 to 59)

            val adjustedCheckInDateTime =
                domain.checkInDateTime.withHourMinute(checkInHour, checkInMinute)
            val adjustedCheckOutDateTime =
                domain.checkOutDateTime.withHourMinute(checkOutHour, checkOutMinute)

            val ownedStay = domain as? Stay.Owned

            return StayEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                vehicleMake = ownedStay?.vehicleMake,
                vehicleColor = ownedStay?.vehicleColor,
                vehicleLicensePlate = ownedStay?.vehicleLicensePlate,
                qtGuests = ownedStay?.qtGuests,
                checkInDateTime = adjustedCheckInDateTime,
                checkOutDateTime = adjustedCheckOutDateTime,
                isAutoCheckIn = ownedStay?.isAutoCheckIn,
                isAutoCheckOut = ownedStay?.isAutoCheckOut,
                totalPrice = ownedStay?.totalPrice,
                commission = ownedStay?.commission,
                observations = domain.observations,
                isCheckInDone = ownedStay?.isCheckInDone,
                isCheckOutDone = ownedStay?.isCheckOutDone,
                isDeleted = domain.isDeleted,
                propertyId = domain.property.propertyId,
                tenantId = ownedStay?.tenant?.tenantId,
                userId = domain.userId,
            )
        }
    }
}
