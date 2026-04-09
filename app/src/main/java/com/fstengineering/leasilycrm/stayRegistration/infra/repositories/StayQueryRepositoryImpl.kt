package com.fstengineering.leasilycrm.stayRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.StayRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.UnavailablePeriodRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.OccupiedPeriod
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toCheckInCheckOutReadModel
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toStayRawReadModel
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toUnavailablePeriodRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class StayQueryRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
) : StayQueryRepository {
    override suspend fun getStayById(id: Long): AppResult<StayRawReadModel, ApplicationError> {
        return databaseDataSource.getById(id = id)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toStayRawReadModel()) },
            )
    }

    override suspend fun getUnavailablePeriodById(
        id: Long,
    ): AppResult<UnavailablePeriodRawReadModel, ApplicationError> {
        return databaseDataSource.getById(id = id)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toUnavailablePeriodRawReadModel()) },
            )
    }

    override suspend fun getOccupiedPeriodsByPropertyId(
        propertyId: Long,
    ): AppResult<ImmutableList<OccupiedPeriod>, ApplicationError> {
        return databaseDataSource.getAllByPropertyId(propertyId = propertyId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = {
                    val dates = it.map { localModel -> localModel.toCheckInCheckOutReadModel() }
                    AppResult.Success(data = dates.toPersistentList())
                },
            )
    }
}
