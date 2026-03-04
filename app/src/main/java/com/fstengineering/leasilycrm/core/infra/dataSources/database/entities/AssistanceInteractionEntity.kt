package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime

@Entity(
    tableName = "assistance_interactions",
    foreignKeys = [
        ForeignKey(
            entity = AssistanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["assistance_id"],
        ),
    ],
    indices = [Index("assistance_id")],
)
data class AssistanceInteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "interaction_date") val interactionDate: AppDate,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    fun toDomain() = AssistanceInteraction.parse(
        id = id,
        date = interactionDate,
        title = title,
        description = description,
    )

    companion object {
        fun fromDomain(domain: AssistanceInteraction, assistanceId: Long) =
            AssistanceInteractionEntity(
                id = domain.id,
                interactionDate = domain.date,
                title = domain.title,
                description = domain.description,
                assistanceId = assistanceId,
            )
    }
}
