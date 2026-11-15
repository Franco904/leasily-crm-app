package com.fstengineering.leasilycrm.imovelOcupacao.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.domain.services.ImovelOccupiedDatesService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.imovelOcupacao.application.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.application.mappers.toValueObject
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodosOcupadosReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.queries.ImovelOcupacaoArgs
import com.fstengineering.leasilycrm.imovelOcupacao.infra.dataSources.ImovelOcupacaoReadDataSource
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WatchImovelOcupacaoUseCase(
    private val imovelOcupacaoReadDataSource: ImovelOcupacaoReadDataSource,
    private val leadTenantSharedQueryRepository: LeadTenantSharedQueryRepository,
) {
    suspend operator fun invoke(
        args: ImovelOcupacaoArgs,
    ): Flow<AppResult<ImovelPeriodosOcupadosReadModel, ApplicationError>> {
        return imovelOcupacaoReadDataSource.watchPeriodosOcupados(imovelId = args.id)
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

                val readModel = ImovelPeriodosOcupadosReadModel(
                    occupiedPeriods = occupiedPeriodsGrouped,
                )

                AppResult.Success(data = readModel)
            }
    }
}
