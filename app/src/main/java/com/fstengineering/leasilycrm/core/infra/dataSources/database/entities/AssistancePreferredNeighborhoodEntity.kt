package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "assistance_preferred_neighborhoods",
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        )
    ],
    indices = [Index("assistance_id")],
)
data class AssistancePreferredNeighborhoodEntity(
    @PrimaryKey val neighborhood: String,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    companion object {
        fun fromDomain(neighborhood: String, assistanceId: Long) =
            AssistancePreferredNeighborhoodEntity(
                neighborhood = neighborhood,
                assistanceId = assistanceId,
            )
    }
}
