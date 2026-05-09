package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceSelectablePropertyReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSelectablePropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers.toSelectablePropertyReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceSelectablePropertyQueryRepositoryImpl(
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : AssistanceSelectablePropertyQueryRepository {
    override suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<AssistanceSelectablePropertyReadModel>, ApplicationError> {
        val selectableProperties = propertyDatabaseDataSource.getAll(matching)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it.map { property -> property.toSelectablePropertyReadModel() } },
            )
            .toPersistentList()

        return AppResult.Success(data = selectableProperties)
    }
}
