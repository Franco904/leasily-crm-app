package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood

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
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val neighborhood: String,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    fun toDomain() = AssistanceSearchedNeighborhood(
        id = id,
        neighborhood = neighborhood,
        assistanceId = assistanceId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: AssistanceSearchedNeighborhood): AssistanceSearchedNeighborhoodEntity {
            requireNotNull(domain.neighborhood) { "AssistanceSearchedNeighborhood.neighborhood is required" }
            requireNotNull(domain.assistanceId) { "AssistanceSearchedNeighborhood.assistanceId is required" }

            return AssistanceSearchedNeighborhoodEntity(
                id = domain.id,
                neighborhood = domain.neighborhood,
                assistanceId = domain.assistanceId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
