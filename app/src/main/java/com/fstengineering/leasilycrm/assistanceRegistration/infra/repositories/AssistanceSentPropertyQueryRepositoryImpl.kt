package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceSentPropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSentPropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.utils.toSentPropertyReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceSentPropertyQueryRepositoryImpl(
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : AssistanceSentPropertyQueryRepository {
    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<ImmutableList<AssistanceSentPropertyReadModel>, ApplicationError> {
        val sentProperties = propertyDatabaseDataSource.getAllSentInAssistance(assistanceId)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it.map { property -> property.toSentPropertyReadModel() } },
            )
            .toPersistentList()

        return AppResult.Success(data = sentProperties)
    }
}
