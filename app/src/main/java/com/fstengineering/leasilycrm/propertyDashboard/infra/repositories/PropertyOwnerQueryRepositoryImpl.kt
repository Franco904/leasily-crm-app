package com.fstengineering.leasilycrm.propertyDashboard.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyOwnerQueryRepository

class PropertyOwnerQueryRepositoryImpl(
    private val databaseDataSource: PropertyOwnerDatabaseDataSource,
) : PropertyOwnerQueryRepository {
    override suspend fun getNameById(
        propertyOwnerId: Long,
    ): AppResult<String, ApplicationError> {
        return databaseDataSource.getNameById(propertyOwnerId)
    }
}
