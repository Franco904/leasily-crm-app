package com.fstengineering.leasilycrm.propertyDashboard.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyRawReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PropertyQueryRepositoryImpl(
    private val databaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override fun watchById(propertyId: Long): Flow<AppResult<PropertyRawReadModel, ApplicationError>> {
        return databaseDataSource.watchById(id = propertyId)
            .map { property ->
                property.fold(
                    onError = { e -> AppResult.Error(error = e.toApplicationError()) },
                    onSuccess = { AppResult.Success(data = it.toRawReadModel()) },
                )
            }
    }
}
