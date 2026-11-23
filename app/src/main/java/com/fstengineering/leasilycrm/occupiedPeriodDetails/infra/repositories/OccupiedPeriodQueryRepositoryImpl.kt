package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainPreferences
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OccupiedPeriodQueryRepositoryImpl(
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val plainLocalStorage: LocalStorage<PlainPreferences>,
) : OccupiedPeriodQueryRepository {
    override fun watchById(stayId: Long): Flow<AppResult<OccupiedPeriodRawReadModel?>> =
        flow {
            val stayFlow = stayDatabaseDataSource.watchById(id = stayId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map { compound ->
                    val raw = compound?.toRawReadModel()
                        ?: return@map AppResult.Success(data = null)

                    AppResult.Success<OccupiedPeriodRawReadModel?>(data = raw)
                }
                .distinctUntilChanged()

            emitAll(stayFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean>> =
        flow {
            plainLocalStorage.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow ->
                        emitAll(
                            prefsFlow.map {
                                val mustNotShow = it.mustNotShowCheckOutDialogAnymore ?: false
                                AppResult.Success(data = mustNotShow)
                            }
                        )
                    }
                )
        }
}
