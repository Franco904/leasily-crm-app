package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal

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
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "vehicle_make") val vehicleMake: String?,
    @ColumnInfo(name = "vehicle_color") val vehicleColor: String?,
    @ColumnInfo(name = "vehicle_license_plate") val vehicleLicensePlate: String?,
    @ColumnInfo(name = "guests") val guests: Int?,
    @ColumnInfo(name = "check_in_date_time") val checkInDateTime: AppDateTime,
    @ColumnInfo(name = "check_out_date_time") val checkOutDateTime: AppDateTime,
    @ColumnInfo(name = "is_auto_check_in") val isAutoCheckIn: Boolean?,
    @ColumnInfo(name = "is_auto_check_out") val isAutoCheckOut: Boolean?,
    @ColumnInfo(name = "total_price") val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val observations: String?,
    val status: Int?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long?,
    val inconsistencies: String?,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
) {
    companion object {
        fun fromDomain(domain: Stay): StayEntity {
            val ownedStay = domain as? Stay.Owned

            return StayEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                vehicleMake = ownedStay?.vehicleMake,
                vehicleColor = ownedStay?.vehicleColor,
                vehicleLicensePlate = ownedStay?.vehicleLicensePlate,
                guests = ownedStay?.guestsCount?.count,
                checkInDateTime = domain.period.checkInDateTime,
                checkOutDateTime = domain.period.checkOutDateTime,
                isAutoCheckIn = ownedStay?.isAutoCheckIn,
                isAutoCheckOut = ownedStay?.isAutoCheckOut,
                totalPrice = ownedStay?.totalPrice,
                commission = ownedStay?.commission,
                observations = domain.observations,
                status = ownedStay?.status?.id,
                isDeleted = false,
                propertyId = domain.propertyId,
                tenantId = ownedStay?.tenantId,
                inconsistencies = domain.inconsistencies.joinToString(", "),
                contractVersion = BuildConfig.CONTRACT_VERSION,
            )
        }
    }
}
