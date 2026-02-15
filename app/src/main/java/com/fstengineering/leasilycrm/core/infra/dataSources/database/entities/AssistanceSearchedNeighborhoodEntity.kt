package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

@Entity(
    tableName = "assistance_searched_neighborhoods",
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        )
    ],
    indices = [Index("assistance_id")],
)
data class AssistanceSearchedNeighborhoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val neighborhood: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    fun toDomain() = AssistanceSearchedNeighborhood(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        neighborhood = neighborhood,
        assistanceId = assistanceId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: AssistanceSearchedNeighborhood) =
            AssistanceSearchedNeighborhoodEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                neighborhood = domain.neighborhood,
                assistanceId = domain.assistanceId,
                isDeleted = domain.isDeleted,
            )
    }
}
