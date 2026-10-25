package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.ProprietarioSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource

class ProprietarioSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val proprietarioDatabaseDataSource: ProprietarioDatabaseDataSource,
) : ProprietarioSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            proprietarioDatabaseDataSource.delete(id)
        }
    }
}
