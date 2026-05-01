package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferencesDataSource

class OccupiedPeriodSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val occupiedPeriodDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PlainPreferencesDataSource,
) : OccupiedPeriodSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            occupiedPeriodDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckInDone.id,
            )
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            occupiedPeriodDatabaseDataSource.updateStatus(id = id, status = StayStatus.Upcoming.id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun registerCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckOutDone.id,
            )
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckInDone.id,
            )
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }
}
