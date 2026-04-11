package com.fstengineering.leasilycrm.propertyOccupation.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.services.PropertyOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyOccupation.application.mappers.toReadModel
import com.fstengineering.leasilycrm.propertyOccupation.application.mappers.toValueObject
import com.fstengineering.leasilycrm.propertyOccupation.application.models.dtos.PropertyOccupiedPeriodsReadModel
import com.fstengineering.leasilycrm.propertyOccupation.application.models.queries.PropertyOccupationArgs
import com.fstengineering.leasilycrm.propertyOccupation.application.repositories.StayQueryRepository
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchPropertyOccupationUseCase(
    private val stayQueryRepository: StayQueryRepository,
    private val leadTenantSharedQueryRepository: LeadTenantSharedQueryRepository,
) : QueryFlowUseCase<PropertyOccupationArgs, PropertyOccupiedPeriodsReadModel> {
    override suspend fun invoke(
        args: PropertyOccupationArgs,
    ): Flow<AppResult<PropertyOccupiedPeriodsReadModel, ApplicationError>> {
        return stayQueryRepository.watchAllByPropertyId(propertyId = args.id)
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

                val occupiedPeriodsGrouped = PropertyOccupiedDatesService
                    .getOccupiedPeriodsGroupedByMonthYear(occupiedPeriods)
                    .mapValues { (_, dates) ->
                        dates.map { date ->
                            val tenantName = date.tenantId?.let(tenantNamesById::get)
                            date.toReadModel(tenantName)
                        }.toPersistentList()
                    }
                    .toPersistentMap()

                val readModel = PropertyOccupiedPeriodsReadModel(
                    occupiedPeriods = occupiedPeriodsGrouped,
                )

                AppResult.Success(data = readModel)
            }
    }
}
