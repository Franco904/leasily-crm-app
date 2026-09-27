package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers.toCheckInCheckOutReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers.toStayRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.mappers.toUnavailablePeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first

class OccupiedPeriodQueryRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
) : OccupiedPeriodQueryRepository {
    override suspend fun getStayById(id: Long): AppResult<StayRawReadModel, ApplicationError> {
        return databaseDataSource.getById(id = id)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toStayRawReadModel()) },
            )
    }

    override suspend fun getUnavailablePeriodById(
        id: Long,
    ): AppResult<UnavailablePeriodRawReadModel, ApplicationError> {
        return databaseDataSource.getById(id = id)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toUnavailablePeriodRawReadModel()) },
            )
    }

    override suspend fun getOccupiedPeriodsByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<OccupiedPeriodReadModel>, ApplicationError> {
        return databaseDataSource.watchAllByPropertyId(propertyId)
            .first()
            .fold(
                onError = { e -> AppResult.Error(error = e) },
                onSuccess = {
                    val dates = it.map { localModel -> localModel.toCheckInCheckOutReadModel() }
                    AppResult.Success(data = dates.toPersistentList())
                },
            )
    }
}
