package com.fstengineering.leasilycrm.stayDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.stayDetails.application.models.dtos.OccupiedPeriodRawReadModel
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.stayDetails.infra.mappers.toRawReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OccupiedPeriodQueryRepositoryImpl(
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : OccupiedPeriodQueryRepository {
    override fun watchById(stayId: Long): Flow<AppResult<OccupiedPeriodRawReadModel?, ApplicationError>> =
        flow {
            val stayFlow = stayDatabaseDataSource.watchById(id = stayId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map { compound ->
                    val raw = compound?.toRawReadModel()
                        ?: return@map AppResult.Success(data = null)

                    AppResult.Success<OccupiedPeriodRawReadModel?, ApplicationError>(data = raw)
                }
                .distinctUntilChanged()

            emitAll(stayFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>> =
        flow {
            plainPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
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
