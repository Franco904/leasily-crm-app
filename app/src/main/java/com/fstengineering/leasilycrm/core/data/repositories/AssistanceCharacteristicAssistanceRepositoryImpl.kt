package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicAssistanceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceCharacteristicAssistanceRepositoryImpl(
    private val dao: AssistanceCharacteristicAssistanceCrossRefDao,
) : AssistanceCharacteristicAssistanceRepository {
    override suspend fun insertOrUpdate(
        characteristicAssistance: AssistanceCharacteristicAssistance,
    ): AppResult<Unit, DataError> {
        val characteristicAssistanceEntity =
            AssistanceCharacteristicAssistanceCrossRefEntity.fromDomain(
                domain = characteristicAssistance,
            )

        dao.insertOrUpdate(characteristicAssistanceEntity)

        return AppResult.Success(data = Unit)
    }
}
