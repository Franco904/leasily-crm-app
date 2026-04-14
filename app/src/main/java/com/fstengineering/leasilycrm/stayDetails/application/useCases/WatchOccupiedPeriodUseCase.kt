package com.fstengineering.leasilycrm.stayDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.services.PropertyOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriod
import com.fstengineering.leasilycrm.stayDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.TenantQueryRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchOccupiedPeriodUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) {
    operator fun invoke(periodId: Long): Flow<AppResult<OccupiedPeriodReadModel?, ApplicationError>> {
        return occupiedPeriodQueryRepository.watchById(periodId).map outer@{ periodResult ->
            val raw = periodResult.fold(
                onError = { e -> return@outer AppResult.Error(error = e) },
                onSuccess = { it },
            ) ?: return@outer AppResult.Success(data = null)

            val property = propertyQueryRepository.getById(raw.propertyId)
                .fold(
                    onError = { e -> return@outer AppResult.Error(error = e) },
                    onSuccess = { it },
                )

            val tenant = raw.tenantId?.let {
                tenantQueryRepository.getById(it)
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { tenant -> tenant },
                    )
            }

            val occupiedDatesGrouped =
                PropertyOccupiedDatesService.getOccupiedPeriodsGroupedByMonthYear(
                    occupiedPeriods = persistentListOf(
                        PropertyOccupiedPeriod(
                            id = raw.id,
                            tenantId = raw.tenantId,
                            checkInDateTime = raw.checkInDateTime,
                            checkOutDateTime = raw.checkOutDateTime,
                        ),
                    ),
                )

            val readModel = OccupiedPeriodReadModel.create(
                raw = raw,
                property = property,
                tenant = tenant,
                occupiedDatesGrouped = occupiedDatesGrouped,
            )

            AppResult.Success(data = readModel)
        }
    }
}
