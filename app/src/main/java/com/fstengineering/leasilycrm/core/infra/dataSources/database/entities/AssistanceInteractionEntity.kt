package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceInteractionReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate

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
    fun toReadModel() = AssistanceInteractionReadModel(
        id = id,
        date = interactionDate,
        title = title,
        description = description,
    )

    companion object {
        fun fromEntity(entity: AssistanceInteraction, assistanceId: Long) =
            AssistanceInteractionEntity(
                id = entity.id,
                interactionDate = entity.date,
                title = entity.title,
                description = entity.description,
                assistanceId = assistanceId,
            )
    }
}
