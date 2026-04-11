package com.fstengineering.leasilycrm.propertyStays.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StayQueryRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
) : StayQueryRepository {
    override fun watchAllByPropertyId(
        propertyId: Long,
    ): Flow<AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError>> {
        return databaseDataSource.watchAllByPropertyId(propertyId)
            .map { result ->
                result.fold(
                    onError = { e -> AppResult.Error(error = e.toApplicationError()) },
                    onSuccess = { occupiedPeriods ->
                        val readModels = occupiedPeriods
                            .map { it.toRawReadModel() }
                            .toPersistentList()
                        AppResult.Success(data = readModels)
                    },
                )
            }
    }
}
