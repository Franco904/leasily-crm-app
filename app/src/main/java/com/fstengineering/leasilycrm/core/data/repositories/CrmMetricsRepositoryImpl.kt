package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.application.models.AppLazyDate
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CrmMetricsDao
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class CrmMetricsRepositoryImpl(
    private val crmMetricsDao: CrmMetricsDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val now: AppLazyDate,
) : CrmMetricsRepository {
    override fun watchGeneratedLeadsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow ->
                        val remoteId = prefsFlow.first().remoteId
                        remoteId ?: run {
                            return@flow emit(AppResult.Error(error = DataSourceError.PreferencesError.NotFound))
                        }
                    }
                )

            val generatedLeadsPerMonthFlow = crmMetricsDao
                .watchGeneratedLeadsPerMonth(userId = userRemoteId)
                .map { generatedLeadsPerMonth ->
                    val generatedLeadsPerMonthMap = generatedLeadsPerMonth
                        .associate {
                            val split = it.monthYear.split("-")

                            val monthYear = CalendarMonthYear(
                                id = split[0].toInt() + split[1].toInt(),
                                month = split[0].toInt(),
                                year = split[1].toInt(),
                            )

                            monthYear to it.leadCount
                        }
                        .toPersistentMap()

                    val generatedLeadsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { 0 }
                        .toMutableMap()

                    generatedLeadsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val leadCount = generatedLeadsPerMonthMap[monthYear]
                        generatedLeadsWithMissingMonthsMap.put(monthYear, leadCount ?: 0)
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>(
                        data = generatedLeadsWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(generatedLeadsPerMonthFlow)
        }

    override fun watchGeneratedLeadsPerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow ->
                        val remoteId = prefsFlow.first().remoteId
                        remoteId ?: run {
                            return@flow emit(AppResult.Error(error = DataSourceError.PreferencesError.NotFound))
                        }
                    }
                )

            val generatedLeadsPerTenantSegmentPerMonthFlow = crmMetricsDao
                .watchGeneratedLeadsPerTenantSegmentPerMonth(userId = userRemoteId)
                .map { generatedLeadsPerTenantSegmentPerMonth ->
                    val generatedLeadsPerTenantSegmentPerMonthMap =
                        generatedLeadsPerTenantSegmentPerMonth
                            .groupBy {
                                val split = it.monthYear.split("-")

                                CalendarMonthYear(
                                    id = split[0].toInt() + split[1].toInt(),
                                    month = split[0].toInt(),
                                    year = split[1].toInt(),
                                )
                            }
                            .mapValues { (_, compounds) ->
                                compounds
                                    .associate { it.customerSegmentName to it.leadCount }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val generatedLeadsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, Int>() }
                        .toMutableMap()

                    generatedLeadsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val leadCountPerTenantSegment =
                            generatedLeadsPerTenantSegmentPerMonthMap[monthYear]
                        generatedLeadsWithMissingMonthsMap
                            .put(monthYear, leadCountPerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>(
                        data = generatedLeadsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(generatedLeadsPerTenantSegmentPerMonthFlow)
        }

    override fun watchLeadContactChannelDistribution(): Flow<AppResult<ImmutableMap<String, Double>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow ->
                        val remoteId = prefsFlow.first().remoteId
                        remoteId ?: run {
                            return@flow emit(AppResult.Error(error = DataSourceError.PreferencesError.NotFound))
                        }
                    }
                )

            val leadContactChannelDistributionFlow = crmMetricsDao
                .watchCountLeadsByContactChannelType(userId = userRemoteId)
                .map { leadContactChannelCounts ->
                    val totalLeads = leadContactChannelCounts.sumOf { it.leadCount }

                    val leadContactChannelDistribution = leadContactChannelCounts
                        .associate {
                            val roundedLeadCount = (it.leadCount.toDouble() / totalLeads) * 100
                            it.leadContactChannelName to roundedLeadCount.roundToInt().toDouble()
                        }
                        .filterValues { it > 0.0 }
                        .toPersistentMap()

                    AppResult.Success<ImmutableMap<String, Double>, DataSourceError>(
                        data = leadContactChannelDistribution,
                    )
                }

            emitAll(leadContactChannelDistributionFlow)
        }
}
