package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Locale

class CrmMetricsRepositoryImpl(
    private val tenantDao: TenantDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : CrmMetricsRepository {
    override suspend fun watchLeadContactChannelDistribution(): Flow<AppResult<ImmutableList<Pair<String, Double>>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow ->
                        prefsFlow.first().remoteId ?: run {
                            emit(AppResult.Error(error = DataSourceError.PreferencesError.NotFound))
                            return@flow
                        }
                    }
                )

            val leadContactChannelDistributionFlow = tenantDao
                .watchCountLeadsByContactChannelType(userId = userRemoteId)
                .map { leadContactChannelCounts ->
                    val totalLeads = leadContactChannelCounts.sumOf { it.leadCount }

                    val leadContactChannelDistribution = leadContactChannelCounts
                        .map {
                            val roundedLeadCount = String.format(
                                Locale.ENGLISH,
                                "%.2f",
                                it.leadCount.toDouble() / totalLeads,
                            ).toDouble()

                            it.leadContactChannelName to roundedLeadCount * 100
                        }
                        .filter { it.second > 0.0 }
                        .toPersistentList()

                    AppResult.Success<ImmutableList<Pair<String, Double>>, DataSourceError>(
                        data = leadContactChannelDistribution,
                    )
                }

            emitAll(leadContactChannelDistributionFlow)
        }
}
