package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.mappers.toReadModel

class PropertyQueryRepositoryImpl(
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override suspend fun getById(propertyId: Long): AppResult<PropertyReadModel, ApplicationError> {
        return propertyDatabaseDataSource.getById(id = propertyId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }
}
