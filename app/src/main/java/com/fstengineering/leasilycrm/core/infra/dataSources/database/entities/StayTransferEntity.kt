package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer

@Entity(
    tableName = "stay_transfers",
    foreignKeys = [
        ForeignKey(
            entity = StayEntity::class,
            parentColumns = ["id"],
            childColumns = ["stay_id"],
        ),
    ],
    indices = [Index("stay_id")],
)
data class StayTransferEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "transfer_date") val transferDate: AppDate,
    @ColumnInfo(name = "transfer_amount") val transferAmount: AppDecimal,
    @ColumnInfo(name = "transfer_description") val transferDescription: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "stay_id") val stayId: Long,
) {
    fun toDomain() = StayTransfer(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        transferDate = transferDate,
        transferAmount = transferAmount,
        transferDescription = transferDescription,
        isDeleted = isDeleted,
        stayId = stayId,
    )

    companion object {
        fun fromDomain(domain: StayTransfer): StayTransferEntity {
            requireNotNull(domain.transferDate) { "StayTransfer.transferDate is required" }
            requireNotNull(domain.transferAmount) { "StayTransfer.transferAmount is required" }
            requireNotNull(domain.stayId) { "StayTransfer.stayId is required" }

            return StayTransferEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                transferDate = domain.transferDate,
                transferAmount = domain.transferAmount,
                transferDescription = domain.transferDescription,
                isDeleted = domain.isDeleted,
                stayId = domain.stayId,
            )
        }
    }
}
