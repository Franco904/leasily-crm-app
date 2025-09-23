package com.fstengineering.leasilycrm.core.data.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import java.time.LocalDate
import java.time.LocalDateTime

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
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "create_date_time") val createDateTime: LocalDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: LocalDateTime,
    @ColumnInfo(name = "interaction_date") val interactionDate: LocalDate,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    fun toDomain() = AssistanceInteraction(
        id = id,
        interactionDate = interactionDate,
        title = title,
        description = description,
        assistanceId = assistanceId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: AssistanceInteraction): AssistanceInteractionEntity {
            requireNotNull(domain.interactionDate) { "AssistanceInteraction.interactionDate is required" }
            requireNotNull(domain.title) { "AssistanceInteraction.title is required" }
            requireNotNull(domain.assistanceId) { "AssistanceInteraction.assistanceId is required" }

            return AssistanceInteractionEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: LocalDateTime.now(),
                updateDateTime = LocalDateTime.now(),
                interactionDate = domain.interactionDate,
                title = domain.title,
                description = domain.description,
                assistanceId = domain.assistanceId,
                isDeleted = domain.isDeleted,
            )
        }
    }
}
