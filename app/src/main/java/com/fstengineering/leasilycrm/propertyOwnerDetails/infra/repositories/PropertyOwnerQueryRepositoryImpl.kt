package com.fstengineering.leasilycrm.propertyOwnerDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerDetails.infra.mappers.toReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerQueryRepositoryImpl(
    private val databaseDataSource: PropertyOwnerDatabaseDataSource,
) : PropertyOwnerQueryRepository {
    override fun watchById(
        propertyOwnerId: Long,
    ): Flow<AppResult<PropertyOwnerReadModel, ApplicationError>> = flow {
        val propertyOwnerFlow = databaseDataSource.watchById(id = propertyOwnerId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it }
            )
            .map { AppResult.Success<PropertyOwnerReadModel, ApplicationError>(data = it.toReadModel()) }
            .distinctUntilChanged()

        emitAll(propertyOwnerFlow)
    }
}
