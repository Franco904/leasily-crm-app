package com.fstengineering.leasilycrm.stayDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodCommandRepository

class OccupiedPeriodCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : OccupiedPeriodCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 1)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 0)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun registerCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 2)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 1)
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
