package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal

@Entity(
    tableName = "stays",
    foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"],
        ),
        ForeignKey(
            entity = LeadTenantEntity::class,
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
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "status") val status: Int?,
    @ColumnInfo(name = "property_id") val propertyId: Long,
    @ColumnInfo(name = "tenant_id") val tenantId: Long?,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
)
