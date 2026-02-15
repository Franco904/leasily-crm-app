package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding

@Entity(
    tableName = "property_owners",
    indices = [Index("user_id")],
)
data class PropertyOwnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val name: String,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "payment_key") val paymentKey: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    companion object {
        fun fromDomain(domain: PropertyOwner) =
            PropertyOwnerEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                name = domain.name.normalizeEncoding(),
                phone = domain.phone,
                email = domain.email,
                paymentKey = domain.paymentKey,
                isDeleted = domain.isDeleted,
                userId = domain.userId,
            )
    }
}
