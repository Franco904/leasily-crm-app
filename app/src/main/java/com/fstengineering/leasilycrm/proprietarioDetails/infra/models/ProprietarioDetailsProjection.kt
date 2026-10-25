package com.fstengineering.leasilycrm.proprietarioDetails.infra.models

import androidx.room.ColumnInfo
import androidx.room.Relation
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm

/** O `id` só existe porque o Room exige a coluna-pai da `@Relation` como campo. */
data class ProprietarioDetailsProjection(
    val id: Long,
    val name: String,
    val phone: String?,
    val email: String?,
    @ColumnInfo(name = "payment_key") val paymentKey: String?,
    @Relation(
        parentColumn = "id",
        entityColumn = "property_owner_id",
        entity = ProprietarioFileOrm::class,
        projection = ["property_owner_file_uri", "property_owner_file_name"],
    )
    val files: List<ProprietarioFileDetailsProjection>,
)

data class ProprietarioFileDetailsProjection(
    @ColumnInfo(name = "property_owner_file_uri") val uriString: String,
    @ColumnInfo(name = "property_owner_file_name") val name: String,
)
