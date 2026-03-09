package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserSessionRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetTodayCheckInsCheckOutsToNotifyUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.application.repositories.SettingsSharedQueryRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
    private val userSessionRepository: UserSessionRepository,
    private val stayRepository: StayRepository,
    private val settingsSharedQueryRepository: SettingsSharedQueryRepository,
    private val now: AppDateTime,
) : GetTodayCheckInsCheckOutsToNotifyUseCase {
    override suspend fun invoke(): AppResult<List<StayCheckNotificationStatus>, InfraError> {
        val isTodayCheckInCheckOutNotificationEnabled =
            settingsSharedQueryRepository.watch().firstOrNull()
                ?.fold(
                    onError = { false },
                    onSuccess = { it.isTodayCheckInCheckOutNotificationEnabled }
                ) ?: false

        val isSessionAlive = userSessionRepository.isSessionAlive()
            .fold(onError = { false }, onSuccess = { it })

        if (now.dateTime().hour > 9 || !isTodayCheckInCheckOutNotificationEnabled || !isSessionAlive) {
            return AppResult.Success(data = emptyList())
        }

        return stayRepository.watchAllUpcomingCheckInsAndCheckOuts().first()
            .fold(
                onError = { _ -> AppResult.Success(data = persistentListOf()) },
                onSuccess = { upcomingCheckInsAndCheckOuts ->
                    val todayCheckInsAndCheckOutsToNotify = upcomingCheckInsAndCheckOuts
                        .filter {
                            it.tenantName != null
                                    && it.propertyTypeId != null
                                    && it.propertyOwnerName != null
                        }
                        .map {
                            val mustNotifyCheckIn =
                                !it.isCheckInDone && it.checkInDateTime.isToday()
                            val mustNotifyCheckOut = it.checkOutDateTime.isToday()

                            val type = PropertyType.fromId(requireNotNull(it.propertyTypeId))

                            StayCheckNotificationStatus(
                                tenantName = requireNotNull(it.tenantName),
                                propertyType = requireNotNull(type),
                                propertyOwnerName = requireNotNull(it.propertyOwnerName),
                                hourMinute = if (mustNotifyCheckIn) {
                                    it.checkInDateTime.toHourMinute()
                                } else it.checkOutDateTime.toHourMinute(),
                                mustNotifyCheckIn = mustNotifyCheckIn,
                                mustNotifyCheckOut = mustNotifyCheckOut,
                            )
                        }
                        .filter { it.mustNotifyCheckIn || it.mustNotifyCheckOut }

                    AppResult.Success(data = todayCheckInsAndCheckOutsToNotify)
                },
            )
    }
}
