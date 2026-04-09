package com.fstengineering.leasilycrm.stayRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyQueryRepositoryImpl(
    private val databaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override suspend fun getById(propertyId: Long): AppResult<PropertyReadModel, ApplicationError> {
        return databaseDataSource.getById(id = propertyId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }

    override suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<PropertyReadModel>, ApplicationError> {
        val properties = databaseDataSource.getAll(matching)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it },
            )
            .map { property -> property.toReadModel() }.toPersistentList()

        return AppResult.Success(data = properties)
    }
}
