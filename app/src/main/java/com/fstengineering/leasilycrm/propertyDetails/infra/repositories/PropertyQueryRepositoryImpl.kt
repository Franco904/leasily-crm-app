package com.fstengineering.leasilycrm.propertyDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PropertyQueryRepositoryImpl(
    private val databaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override fun watchById(propertyId: Long): Flow<AppResult<PropertyReadModel, ApplicationError>> {
        return databaseDataSource.watchById(id = propertyId)
            .map { property ->
                property.fold(
                    onError = { e -> AppResult.Error(error = e) },
                    onSuccess = { AppResult.Success(data = it.toRawReadModel()) },
                )
            }
    }
}
