package com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.mappers.toReadModel

class PropertyOwnerQueryRepositoryImpl(
    private val databaseDataSource: PropertyOwnerDatabaseDataSource,
) : PropertyOwnerQueryRepository {
    override suspend fun getById(
        propertyOwnerId: Long,
    ): AppResult<PropertyOwnerReadModel, ApplicationError> {
        return databaseDataSource.getByIdCompound(id = propertyOwnerId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) }
            )
    }
}
