package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.models.OccupiedPeriodNotificationReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList

interface OccupiedPeriodSharedQueryRepository {
    fun getAllTodayCheckInsAndCheckOutsToNotify(): AppResult<ImmutableList<OccupiedPeriodNotificationReadModel>, ApplicationError>
}
