package com.fstengineering.leasilycrm.proprietarioRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioFileDatabaseDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories.ProprietarioCommandRepository
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toLocalDataModel

class ProprietarioCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val proprietarioDatabaseDataSource: ProprietarioDatabaseDataSource,
    private val proprietarioFileDatabaseDataSource: ProprietarioFileDatabaseDataSource,
) : ProprietarioCommandRepository {
    override suspend fun insertOrUpdate(
        proprietario: Proprietario,
    ): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            saveDataInDatabase(proprietario)
        }
    }

    private suspend fun saveDataInDatabase(proprietario: Proprietario) {
        val ownerLocalModel = proprietario.toLocalDataModel()

        val proprietarioId = proprietarioDatabaseDataSource.insertOrUpdate(ownerLocalModel)
            .let { id -> proprietario.id ?: id }

        proprietario.files.forEach {
            val fileLocalModel = it.toLocalDataModel(proprietarioId)
            proprietarioFileDatabaseDataSource.insertOrUpdate(fileLocalModel)
        }
    }
}
