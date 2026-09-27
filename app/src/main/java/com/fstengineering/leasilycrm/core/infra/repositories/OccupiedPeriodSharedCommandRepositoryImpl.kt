package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayStatus
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences

class OccupiedPeriodSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val occupiedPeriodDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : OccupiedPeriodSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            occupiedPeriodDatabaseDataSource.delete(id)
        }
    }

    override suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckInDone.id,
            )
        }
    }

    override suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            occupiedPeriodDatabaseDataSource.updateStatus(id = id, status = StayStatus.Upcoming.id)
        }
    }

    override suspend fun registerCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckOutDone.id,
            )
        }
    }

    override suspend fun undoRegisterCheckOut(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            occupiedPeriodDatabaseDataSource.updateStatus(
                id = id,
                status = StayStatus.CheckInDone.id,
            )
        }
    }

    override suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError> {
        return plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore)
        }
    }
}
