package com.fstengineering.leasilycrm.propertyDashboard.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class StayQueryRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
) : StayQueryRepository {
    override suspend fun getAllByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError> {
        return databaseDataSource.getAllByPropertyId(propertyId)
            .fold(
                onError = { e -> AppResult.Error(error = e.toApplicationError()) },
                onSuccess = { stays ->
                    val readModels = stays.map { it.toRawReadModel() }.toPersistentList()
                    AppResult.Success(data = readModels)
                },
            )
    }
}
