package com.fstengineering.leasilycrm.propertyDashboard.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.services.PropertyOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.propertyDashboard.application.mappers.toReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.mappers.toValueObject
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedDateReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WatchPropertyUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
    private val stayQueryRepository: StayQueryRepository,
    private val leadTenantSharedQueryRepository: LeadTenantSharedQueryRepository,
    private val dateProvider: AppDateProvider,
) : QueryFlowUseCase<PropertyArgs, PropertyReadModel> {
    override suspend fun invoke(
        args: PropertyArgs,
    ): Flow<AppResult<PropertyReadModel, ApplicationError>> {
        val propertyFlow = propertyQueryRepository.watchById(propertyId = args.id)
        val occupiedPeriodsFlow = stayQueryRepository.watchAllByPropertyId(args.id)

        return propertyFlow.combine(occupiedPeriodsFlow) { propertyResult, occupiedPeriodsResult ->
            propertyResult to occupiedPeriodsResult
        }
            .map outer@{ (propertyResult, occupiedPeriodsResult) ->
                val propertyRaw = propertyResult
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { it },
                    )

                val owner = propertyOwnerQueryRepository.getNameById(
                    propertyOwnerId = propertyRaw.ownerId,
                )
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { name -> PropertyOwnerReadModel(propertyRaw.ownerId, name) },
                    )

                val currentMonthOccupiedPeriods =
                    getCurrentMonthOccupiedPeriods(occupiedPeriodsResult)
                        .fold(
                            onError = { e -> return@outer AppResult.Error(error = e) },
                            onSuccess = { name -> name },
                        )

                val updatedProperty = PropertyReadModel.create(
                    propertyRaw = propertyRaw,
                    owner = owner,
                    currentMonthOccupiedPeriods = currentMonthOccupiedPeriods,
                )

                AppResult.Success(data = updatedProperty)
            }
    }

    private suspend fun getCurrentMonthOccupiedPeriods(
        occupiedPeriodsResult: AppResult<ImmutableList<PropertyOccupiedPeriodRawReadModel>, ApplicationError>,
    ): AppResult<Pair<AppYearMonth, ImmutableList<PropertyOccupiedDateReadModel>>, ApplicationError> {
        val occupiedPeriods = occupiedPeriodsResult
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { periods -> periods.map { it.toValueObject() }.toPersistentList() },
            )

        val tenantNamesById = occupiedPeriods
            .mapNotNull { it.tenantId }
            .distinct()
            .associateWith { tenantId ->
                leadTenantSharedQueryRepository.getNameById(tenantId = tenantId)
                    .fold(
                        onError = { e -> return AppResult.Error(error = e) },
                        onSuccess = { it },
                    )
            }

        val now = dateProvider.now()
        val currentYearMonth = AppYearMonth.of(now.year, now.month)

        val occupiedPeriodsGrouped = PropertyOccupiedDatesService
            .getOccupiedPeriodsGroupedByMonthYear(occupiedPeriods)

        val currentMonthOccupiedPeriods = occupiedPeriodsGrouped[currentYearMonth]
            ?.map { date ->
                val tenantName = date.tenantId?.let(tenantNamesById::get)
                date.toReadModel(tenantName)
            }
            ?.toPersistentList()
            ?: persistentListOf()

        return AppResult.Success(data = currentYearMonth to currentMonthOccupiedPeriods)
    }
}
