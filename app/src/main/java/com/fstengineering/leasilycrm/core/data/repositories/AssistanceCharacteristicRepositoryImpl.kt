package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicDao
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceCharacteristicRepositoryImpl(
    private val dao: AssistanceCharacteristicDao,
) : AssistanceCharacteristicRepository {
    override suspend fun getAll(): AppResult<ImmutableList<AssistanceCharacteristic>, DataError> {
        val assistanceCharacteristics = dao.getAll()
            .map { it.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = assistanceCharacteristics)
    }
}
