package com.fstengineering.leasilycrm.core.infra.dataSources.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

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
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    @ColumnInfo(name = "interaction_date") val interactionDate: AppDate,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "assistance_id") val assistanceId: Long,
) {
    fun toDomain() = AssistanceInteraction(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        interactionDate = interactionDate,
        title = title,
        description = description,
        assistanceId = assistanceId,
        isDeleted = isDeleted,
    )

    companion object {
        fun fromDomain(domain: AssistanceInteraction) =
            AssistanceInteractionEntity(
                id = domain.id,
                createDateTime = domain.createDateTime ?: AppDateTime.now(),
                updateDateTime = AppDateTime.now(),
                interactionDate = domain.interactionDate,
                title = domain.title,
                description = domain.description,
                assistanceId = domain.assistanceId,
                isDeleted = domain.isDeleted,
            )
    }
}
