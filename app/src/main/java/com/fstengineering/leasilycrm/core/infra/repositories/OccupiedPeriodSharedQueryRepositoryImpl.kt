package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.models.OccupiedPeriodNotificationReadModel
import com.fstengineering.leasilycrm.core.application.models.StayCheckInCheckOutCountsReadModel
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OccupiedPeriodSharedQueryRepositoryImpl(
    private val occupiedPeriodDatabaseDataSource: StayDatabaseDataSource,
) : OccupiedPeriodSharedQueryRepository {
    override fun getAllTodayCheckInsAndCheckOutsToNotify(): AppResult<ImmutableList<OccupiedPeriodNotificationReadModel>, ApplicationError> {
        return occupiedPeriodDatabaseDataSource
            .getAllTodayCheckInsAndCheckOutsToNotify()
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { notificationProjections ->
                    val readModels = notificationProjections
                        .map { it.toReadModel() }
                        .toPersistentList()

                    AppResult.Success(data = readModels)
                },
            )
    }

    override fun watchAllPastCheckInCheckOutNotDoneCounts(): Flow<AppResult<StayCheckInCheckOutCountsReadModel, ApplicationError>> {
        return occupiedPeriodDatabaseDataSource
            .watchAllPastCheckInCheckOutNotDoneCounts()
            .map { result ->
                result.fold(
                    onError = { e -> AppResult.Error(error = e.toApplicationError()) },
                    onSuccess = { counts ->
                        AppResult.Success(
                            data = StayCheckInCheckOutCountsReadModel(
                                staysAbleToCheckInCount = counts.staysAbleToCheckInCount,
                                staysAbleToCheckOutCount = counts.staysAbleToCheckOutCount,
                            )
                        )
                    },
                )
            }
    }
}
