package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.models.OccupiedPeriodNotificationReadModel
import com.fstengineering.leasilycrm.core.application.models.StayCheckInCheckOutCountsReadModel
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface OccupiedPeriodSharedQueryRepository {
    fun getAllTodayCheckInsAndCheckOutsToNotify(): AppResult<ImmutableList<OccupiedPeriodNotificationReadModel>, ApplicationError>

    fun watchAllPastCheckInCheckOutNotDoneCounts(): Flow<AppResult<StayCheckInCheckOutCountsReadModel, ApplicationError>>
}
