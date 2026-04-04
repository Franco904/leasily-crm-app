package com.fstengineering.leasilycrm.stayRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.StayCommandRepository
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.Stay
import com.fstengineering.leasilycrm.stayRegistration.domain.entities.UnavailablePeriod
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toLocalDataModel

class StayCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val stayFileDatabaseDataSource: StayFileDatabaseDataSource,
) : StayCommandRepository {
    override suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(stay)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private suspend fun saveDataInDatabase(stay: Stay) {
        val stayLocalModel = stay.toLocalDataModel()

        val stayId = stayDatabaseDataSource.insertOrUpdate(stayLocalModel)
            .let { stay.id ?: it }

        stay.files.forEach {
            val file = it.toLocalDataModel(stayId)
            stayFileDatabaseDataSource.insertOrUpdate(file)
        }
    }

    override suspend fun insertOrUpdate(unavailablePeriod: UnavailablePeriod): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.insertOrUpdate(unavailablePeriod.toLocalDataModel())
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }
}
