package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSentPropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceSentPropertyRepositoryImpl(
    private val databaseDataSource: AssistanceSentPropertyCrossRefDatabaseDataSource,
) : AssistanceSentPropertyRepository {
    override suspend fun insertOrUpdate(
        sentProperty: AssistanceSentProperty,
    ): AppResult<Unit, DataError> {
        val sentPropertyEntity = AssistanceSentPropertyCrossRefEntity.fromDomain(
            domain = sentProperty,
        )

        databaseDataSource.insertOrUpdate(sentPropertyEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
