package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSentProperty
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSentPropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceSentPropertyRepositoryImpl(
    private val dao: AssistanceSentPropertyCrossRefDao,
) : AssistanceSentPropertyRepository {
    override suspend fun insertOrUpdate(
        sentProperty: AssistanceSentProperty,
    ): AppResult<Unit, DataError> {
        val sentPropertyEntity = AssistanceSentPropertyCrossRefEntity.fromDomain(
            domain = sentProperty,
        )

        dao.insertOrUpdate(sentPropertyEntity)

        return AppResult.Success(data = Unit)
    }
}
