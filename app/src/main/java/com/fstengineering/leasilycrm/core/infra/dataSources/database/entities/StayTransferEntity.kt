package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal

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
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "transfer_date") val transferDate: AppDate,
    @ColumnInfo(name = "amount") val amount: AppDecimal,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "stay_id") val stayId: Long,
) {
    fun toDomain() = StayTransfer(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        transferDate = transferDate,
        amount = amount,
        description = description,
        isDeleted = isDeleted,
        stayId = stayId,
    )

    companion object {
        fun fromDomain(domain: StayTransfer) =
            StayTransferEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                transferDate = domain.transferDate,
                amount = domain.amount,
                description = domain.description,
                isDeleted = domain.isDeleted,
                stayId = domain.stayId,
            )
    }
}
