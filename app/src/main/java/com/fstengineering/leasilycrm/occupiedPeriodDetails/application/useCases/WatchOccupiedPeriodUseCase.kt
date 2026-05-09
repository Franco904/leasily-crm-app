package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.services.PropertyOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriod
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries.OccupiedPeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchOccupiedPeriodUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) : QueryFlowUseCase<OccupiedPeriodArgs, OccupiedPeriodReadModel?> {
    override suspend fun invoke(args: OccupiedPeriodArgs): Flow<AppResult<OccupiedPeriodReadModel?, ApplicationError>> {
        return occupiedPeriodQueryRepository.watchById(args.periodId).map outer@{ periodResult ->
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
