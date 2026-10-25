package com.fstengineering.leasilycrm.proprietarioRegistration.infra.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm

data class ProprietarioRegistrationProjection(
    val id: Long,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val name: String,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "payment_key") val paymentKey: String?,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = ProprietarioFileOrm::class,
        projection = ["id", "property_owner_file_uri", "property_owner_file_name"],
    )
    val files: List<ProprietarioFileRegistrationProjection>,
)

data class ProprietarioFileRegistrationProjection(
    val id: Long,
    @ColumnInfo(name = "property_owner_file_uri") val uriString: String,
    @ColumnInfo(name = "property_owner_file_name") val name: String,
)
