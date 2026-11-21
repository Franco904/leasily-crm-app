package com.fstengineering.leasilycrm.imovelDashboard.application.useCases

import com.fstengineering.leasilycrm.core.infra.dataSources.database.ClienteSharedReadDataSource
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.services.ImovelOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.imovelDashboard.application.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelPeriodoOcupado
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources.ImovelDashboardReadDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class WatchImovelUseCase(
    private val imovelDashboardReadDataSource: ImovelDashboardReadDataSource,
    private val clienteSharedReadDataSource: ClienteSharedReadDataSource,
    private val dateProvider: AppDateProvider,
) {
    suspend operator fun invoke(
        args: ImovelArgs,
    ): Flow<AppResult<ImovelReadModel>> {
        val imovelFlow = imovelDashboardReadDataSource.watchImovel(imovelId = args.id)
        val occupiedPeriodsFlow =
            imovelDashboardReadDataSource.watchPeriodosOcupados(imovelId = args.id)

        return imovelFlow.combine(occupiedPeriodsFlow) { imovelResult, occupiedPeriodsResult ->
            imovelResult to occupiedPeriodsResult
        }
            .map outer@{ (imovelResult, occupiedPeriodsResult) ->
                val imovelSummary = imovelResult
                    .fold(
                        onError = { e -> return@outer AppResult.Error(error = e) },
                        onSuccess = { it },
                    )

                val currentMonthOccupiedPeriods =
                    getCurrentMonthOccupiedPeriods(occupiedPeriodsResult)
                        .fold(
                            onError = { e -> return@outer AppResult.Error(error = e) },
                            onSuccess = { it },
                        )

                AppResult.Success(
                    data = imovelSummary.toReadModel(currentMonthOccupiedPeriods),
                )
            }
    }

    private suspend fun getCurrentMonthOccupiedPeriods(
        occupiedPeriodsResult: AppResult<List<ImovelPeriodoOcupado>>,
    ): AppResult<Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>> {
        val occupiedPeriods = occupiedPeriodsResult
            .fold(
                onError = { e -> return AppResult.Error(error = e) },
                onSuccess = { periods -> periods.toPersistentList() },
            )

        val tenantNamesById = occupiedPeriods
            .mapNotNull { it.tenantId }
            .distinct()
            .associateWith { tenantId ->
                clienteSharedReadDataSource.getName(clienteId = tenantId)
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
