package com.fstengineering.leasilycrm.propertyDashboard.application.useCases

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.services.PropertyOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.propertyDashboard.application.mappers.toReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.mappers.toValueObject
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOccupiedDateReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchPropertyUseCase(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
    private val stayQueryRepository: StayQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
    private val now: AppDate,
) : QueryFlowUseCase<PropertyArgs, PropertyReadModel> {
    override suspend fun invoke(
        args: PropertyArgs,
    ): Flow<AppResult<PropertyReadModel, ApplicationError>> {
        return propertyQueryRepository.watchById(propertyId = args.id)
            .map outer@{ storedPropertyResult ->
                val propertyRaw = storedPropertyResult
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

                val currentMonthOccupiedPeriods = getCurrentMonthOccupiedPeriods(
                    propertyId = args.id,
                )
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
        propertyId: Long,
    ): AppResult<Pair<AppYearMonth, ImmutableList<PropertyOccupiedDateReadModel>>, ApplicationError> {
        val occupiedPeriods = stayQueryRepository.getAllByPropertyId(propertyId)
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { periods ->
                    periods.map { it.toValueObject() }.toPersistentList()
                },
            )

        val tenantNamesById = occupiedPeriods
            .mapNotNull { it.tenantId }
            .distinct()
            .associateWith { tenantId ->
                tenantQueryRepository.getNameById(tenantId = tenantId)
                    .fold(
                        onError = { e -> return AppResult.Error(error = e) },
                        onSuccess = { it },
                    )
            }

        val now = now.date()
        val currentYearMonth = AppYearMonth.of(now.year, now.monthValue)

        val currentMonthOccupiedPeriods = PropertyOccupiedDatesService
            .getOccupiedPeriodsGroupedByMonthYear(occupiedPeriods)
            .filter { (yearMonth, _) -> yearMonth == currentYearMonth }
            .mapValues { (_, dates) ->
                dates.map { date ->
                    val tenantName = date.tenantId?.let(tenantNamesById::get)
                    date.toReadModel(tenantName)
                }.toPersistentList()
            }
            .entries
            .firstOrNull()
            ?.toPair() ?: (currentYearMonth to persistentListOf())

        return AppResult.Success(data = currentMonthOccupiedPeriods)
    }
}
