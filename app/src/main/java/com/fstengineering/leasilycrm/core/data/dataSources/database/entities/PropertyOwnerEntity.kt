package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
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
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "user_id") val userId: Long,
) {
    companion object {
        fun fromDomain(domain: PropertyOwner): PropertyOwnerEntity {
            requireNotNull(domain.name) { "PropertyOwner.name is required" }
            requireNotNull(domain.userId) { "PropertyOwner.userId is required" }

            return PropertyOwnerEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                name = domain.name.normalizeEncoding(),
                phone = domain.phone,
                email = domain.email,
                paymentKey = domain.paymentKey,
                userId = domain.userId,
            )
        }
    }
}
