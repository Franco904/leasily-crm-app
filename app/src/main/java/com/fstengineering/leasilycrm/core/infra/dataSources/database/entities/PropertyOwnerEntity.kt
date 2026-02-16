package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding

@Entity(
    tableName = "property_owners",
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
    val inconsistencies: String?,
    @ColumnInfo(name = "contract_version") val contractVersion: Int,
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
                inconsistencies = domain.inconsistencies,
                contractVersion = BuildConfig.CONTRACT_VERSION,
            )
    }
}
