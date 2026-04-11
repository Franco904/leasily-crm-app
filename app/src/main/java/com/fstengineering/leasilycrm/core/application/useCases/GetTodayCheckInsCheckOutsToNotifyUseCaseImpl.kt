package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayCheckNotificationStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

class GetTodayCheckInsCheckOutsToNotifyUseCaseImpl(
    private val userSessionSharedStore: UserSessionSharedStore,
    private val staySharedCommandRepository: StaySharedCommandRepository,
    private val settingsSharedStore: SettingsSharedStore,
    private val now: AppDateTime,
) : GetTodayCheckInsCheckOutsToNotifyUseCase {
    override suspend fun invoke(): AppResult<List<StayCheckNotificationStatus>, InfraError> {
        val isTodayCheckInCheckOutNotificationEnabled =
            settingsSharedStore.watch().firstOrNull()?.isTodayCheckInCheckOutNotificationEnabled
                ?: false

        val isSessionAlive = userSessionSharedStore.isSessionAlive()

        if (now.dateTime().hour > 9 || !isTodayCheckInCheckOutNotificationEnabled || !isSessionAlive) {
            return AppResult.Success(data = emptyList())
        }

        return staySharedCommandRepository.watchAllUpcomingCheckInsAndCheckOuts().first()
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
