package com.fstengineering.leasilycrm.imovelOccupation.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.imovelOccupation.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelOccupation.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.imovelOccupation.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OccupiedPeriodQueryRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
) : OccupiedPeriodQueryRepository {
    override fun watchAllByImovelId(
        imovelId: Long,
    ): Flow<AppResult<ImmutableList<ImovelPeriodoOcupadoRawReadModel>, ApplicationError>> {
        return databaseDataSource.watchAllByImovelId(imovelId)
            .map { result ->
                result.fold(
                    onError = { e -> AppResult.Error(error = e) },
                    onSuccess = { occupiedPeriods ->
                        val readModels = occupiedPeriods
                            .map { it.toRawReadModel() }
                            .toPersistentList()
                        AppResult.Success(data = readModels)
                    },
                )
            }
    }
}
