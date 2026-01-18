package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristicAssistance
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicAssistanceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceCharacteristicAssistanceRepositoryImpl(
    private val databaseDataSource: AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource,
) : AssistanceCharacteristicAssistanceRepository {
    override suspend fun insertOrUpdate(
        characteristicAssistance: AssistanceCharacteristicAssistance,
    ): AppResult<Unit, DataError> {
        val characteristicAssistanceEntity =
            AssistanceCharacteristicAssistanceCrossRefEntity.fromDomain(
                domain = characteristicAssistance,
            )

        databaseDataSource.insertOrUpdate(characteristicAssistanceEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
