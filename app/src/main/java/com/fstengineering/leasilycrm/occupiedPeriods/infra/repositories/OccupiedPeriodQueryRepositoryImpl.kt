package com.fstengineering.leasilycrm.occupiedPeriods.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.occupiedPeriods.application.models.dtos.OccupiedPeriodListReadModel
import com.fstengineering.leasilycrm.occupiedPeriods.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriods.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class OccupiedPeriodQueryRepositoryImpl(
    private val occupiedPeriodDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : OccupiedPeriodQueryRepository {
    override fun watchAll(): Flow<AppResult<ImmutableList<OccupiedPeriodListReadModel>, ApplicationError>> =
        flow {
            val staysFlow = occupiedPeriodDatabaseDataSource.watchAllWithDetails()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val periods =
                        it.map { projection -> projection.toReadModel() }.toPersistentList()
                    AppResult.Success<ImmutableList<OccupiedPeriodListReadModel>, ApplicationError>(
                        data = periods
                    )
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>> =
        flow {
            plainPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { prefsFlow ->
                        emitAll(
                            prefsFlow.map {
                                AppResult.Success(
                                    data = it.mustNotShowCheckOutDialogAnymore ?: false
                                )
                            }
                        )
                    }
                )
        }
}
