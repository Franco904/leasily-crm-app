package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity

interface AssistanceSentImovelCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceSentImovelCrossRefEntity,
    )
}
