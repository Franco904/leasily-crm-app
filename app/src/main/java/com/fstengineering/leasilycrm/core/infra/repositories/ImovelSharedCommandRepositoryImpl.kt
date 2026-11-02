package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.ImovelSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource

class ImovelSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val imovelDatabaseDataSource: ImovelDatabaseDataSource,
) : ImovelSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            imovelDatabaseDataSource.delete(id)
        }
    }
}
