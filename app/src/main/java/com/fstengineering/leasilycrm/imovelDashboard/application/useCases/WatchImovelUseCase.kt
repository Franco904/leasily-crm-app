package com.fstengineering.leasilycrm.imovelDashboard.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.services.ImovelOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.imovelDashboard.application.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.mappers.toValueObject
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelDashboard.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelDashboard.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources.ProprietarioReadDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WatchImovelUseCase(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val proprietarioReadDataSource: ProprietarioReadDataSource,
    private val stayQueryRepository: StayQueryRepository,
    private val leadTenantSharedQueryRepository: LeadTenantSharedQueryRepository,
    private val dateProvider: AppDateProvider,
) {
    suspend operator fun invoke(
        args: ImovelArgs,
    ): Flow<AppResult<ImovelReadModel, ApplicationError>> {
        val imovelFlow = imovelQueryRepository.watchById(imovelId = args.id)
        val occupiedPeriodsFlow = stayQueryRepository.watchAllByImovelId(args.id)

        return imovelFlow.combine(occupiedPeriodsFlow) { imovelResult, occupiedPeriodsResult ->
            imovelResult to occupiedPeriodsResult
        }
            .map outer@{ (imovelResult, occupiedPeriodsResult) ->
                val imovelRaw = imovelResult
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { it },
                    )

                val proprietario = proprietarioReadDataSource.getProprietario(imovelRaw.proprietarioId)
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { it },
                    )

                val currentMonthOccupiedPeriods =
                    getCurrentMonthOccupiedPeriods(occupiedPeriodsResult)
                        .fold(
                            onError = { e -> return@outer AppResult.Error(error = e) },
                            onSuccess = { name -> name },
                        )

                val updatedImovel = ImovelReadModel.create(
                    imovelRaw = imovelRaw,
                    proprietario = proprietario,
                    currentMonthOccupiedPeriods = currentMonthOccupiedPeriods,
                )

                AppResult.Success(data = updatedImovel)
            }
    }

    private suspend fun getCurrentMonthOccupiedPeriods(
        occupiedPeriodsResult: AppResult<ImmutableList<ImovelOccupiedPeriodRawReadModel>, ApplicationError>,
    ): AppResult<Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>, ApplicationError> {
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

        val occupiedPeriodsGrouped = ImovelOccupiedDatesService
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
