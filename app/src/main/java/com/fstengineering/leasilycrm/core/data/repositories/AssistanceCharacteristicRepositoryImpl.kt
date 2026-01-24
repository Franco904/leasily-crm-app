package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceCharacteristicRepositoryImpl(
    private val databaseDataSource: AssistanceCharacteristicDatabaseDataSource,
) : AssistanceCharacteristicRepository {
    override suspend fun getAll(): AppResult<ImmutableList<AssistanceCharacteristic>, DataError> {
        val assistanceCharacteristics = databaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { characteristics ->
                    characteristics.map { it.toDomain() }
                        .toPersistentList()
                }
            )

        return AppResult.Success(data = assistanceCharacteristics)
    }
}
