package com.fstengineering.leasilycrm.propertyRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.propertyRegistration.application.models.PropertyRawReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyQueryRepositoryImpl(
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override suspend fun getById(propertyId: Long): AppResult<PropertyRawReadModel, ApplicationError> {
        return propertyDatabaseDataSource.getById(id = propertyId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toRawReadModel()) },
            )
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError> {
        val neighborHoodNames =
            propertyDatabaseDataSource.getAllNeighborhoodNames()
                .fold(
                    onError = { return AppResult.Error(error = it.toApplicationError()) },
                    onSuccess = { it },
                )
                .toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }
}
