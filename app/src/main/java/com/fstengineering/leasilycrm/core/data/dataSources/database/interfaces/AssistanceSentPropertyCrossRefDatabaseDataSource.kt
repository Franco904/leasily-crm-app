package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity

interface AssistanceSentPropertyCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceSentPropertyCrossRefEntity,
    )
}
