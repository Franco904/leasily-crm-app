package com.fstengineering.leasilycrm.imovelOccupation.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.domain.services.ImovelOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.imovelOccupation.application.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelOccupation.application.mappers.toValueObject
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.imovelOccupation.application.models.queries.ImovelOccupationArgs
import com.fstengineering.leasilycrm.imovelOccupation.application.repositories.OccupiedPeriodQueryRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchImovelOccupationUseCase(
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val leadTenantSharedQueryRepository: LeadTenantSharedQueryRepository,
) {
    suspend operator fun invoke(
        args: ImovelOccupationArgs,
    ): Flow<AppResult<ImovelOccupiedPeriodsReadModel, ApplicationError>> {
        return occupiedPeriodQueryRepository.watchAllByImovelId(imovelId = args.id)
            .map { occupiedPeriodsResult ->
                val occupiedPeriods = occupiedPeriodsResult
                    .fold(
                        onError = { e -> return@map AppResult.Error(error = e) },
                        onSuccess = { periods ->
                            periods.map { it.toValueObject() }.toPersistentList()
                        },
                    )

                val tenantNamesById = occupiedPeriods
                    .mapNotNull { it.tenantId }
                    .distinct()
                    .associateWith { tenantId ->
                        leadTenantSharedQueryRepository.getNameById(tenantId = tenantId)
                            .fold(
                                onError = { e -> return@map AppResult.Error(error = e) },
                                onSuccess = { it },
                            )
                    }

                val occupiedPeriodsGrouped = ImovelOccupiedDatesService
                    .getOccupiedPeriodsGroupedByMonthYear(occupiedPeriods)
                    .mapValues { (_, dates) ->
                        dates.map { date ->
                            val tenantName = date.tenantId?.let(tenantNamesById::get)
                            date.toReadModel(tenantName)
                        }.toPersistentList()
                    }
                    .toPersistentMap()

                val readModel = ImovelOccupiedPeriodsReadModel(
                    occupiedPeriods = occupiedPeriodsGrouped,
                )

                AppResult.Success(data = readModel)
            }
    }
}
