package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity

interface AssistanceSentPropertyCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceSentPropertyCrossRefEntity,
    )
}
