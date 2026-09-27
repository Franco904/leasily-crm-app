package com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerCommandRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.propertyOwnerRegistration.infra.mappers.toLocalDataModel

class PropertyOwnerCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyOwnerDatabaseDataSource: PropertyOwnerDatabaseDataSource,
    private val propertyOwnerFileDatabaseDataSource: PropertyOwnerFileDatabaseDataSource,
) : PropertyOwnerCommandRepository {
    override suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            saveDataInDatabase(propertyOwner)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private suspend fun saveDataInDatabase(propertyOwner: PropertyOwner) {
        val ownerLocalModel = propertyOwner.toLocalDataModel()

        val propertyOwnerId = propertyOwnerDatabaseDataSource.insertOrUpdate(ownerLocalModel)
            .let { id -> propertyOwner.id ?: id }

        propertyOwner.files.forEach {
            val fileLocalModel = it.toLocalDataModel(propertyOwnerId)
            propertyOwnerFileDatabaseDataSource.insertOrUpdate(fileLocalModel)
        }
    }
}
