package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CrmMetricsRepository {
    suspend fun watchLeadContactChannelDistribution(): Flow<AppResult<ImmutableList<Pair<String, Double>>, DataSourceError>>
}
