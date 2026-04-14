package com.fstengineering.leasilycrm.core.application.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayCheckNotificationStatusReadModel
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface GetTodayCheckInsCheckOutsToNotifyUseCase {
    suspend operator fun invoke(): AppResult<List<StayCheckNotificationStatusReadModel>, InfraError>
}
