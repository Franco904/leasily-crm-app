package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases

import com.fstengineering.leasilycrm.core.domain.services.ImovelOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries.OccupiedPeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchOccupiedPeriodUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val imovelQueryRepository: ImovelQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
) {
    suspend operator fun invoke(args: OccupiedPeriodArgs): Flow<AppResult<OccupiedPeriodReadModel?>> {
        return occupiedPeriodQueryRepository.watchById(args.periodId).map outer@{ periodResult ->
            val raw = periodResult.fold(
                onError = { e -> return@outer AppResult.Error(error = e) },
                onSuccess = { it },
            ) ?: return@outer AppResult.Success(data = null)

            val imovel = imovelQueryRepository.getById(raw.imovelId)
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
                ImovelOccupiedDatesService.getOccupiedPeriodsGroupedByMonthYear(
                    occupiedPeriods = persistentListOf(
                        ImovelPeriodoOcupado(
                            id = raw.id,
                            tenantId = raw.tenantId,
                            checkInDateTime = raw.checkInDateTime,
                            checkOutDateTime = raw.checkOutDateTime,
                        ),
                    ),
                )

            val readModel = OccupiedPeriodReadModel.create(
                raw = raw,
                imovel = imovel,
                tenant = tenant,
                occupiedDatesGrouped = occupiedDatesGrouped,
            )

            AppResult.Success(data = readModel)
        }
    }
}
