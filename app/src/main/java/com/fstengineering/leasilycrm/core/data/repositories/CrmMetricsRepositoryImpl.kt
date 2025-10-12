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
                        val leadsCount = generatedLeadsPerMonthMap[monthYear]
                        generatedLeadsWithMissingMonthsMap.put(monthYear, leadsCount ?: 0)
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
                        val leadsCountPerTenantSegment =
                            generatedLeadsPerTenantSegmentPerMonthMap[monthYear]
                        generatedLeadsWithMissingMonthsMap
                            .put(monthYear, leadsCountPerTenantSegment ?: persistentMapOf())
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
                            val roundedLeadsCount = (it.leadCount.toDouble() / totalLeads) * 100
                            it.leadContactChannelName to roundedLeadsCount.roundToInt().toDouble()
                        }
                        .filterValues { it > 0.0 }
                        .toPersistentMap()

                    AppResult.Success<ImmutableMap<String, Double>, DataSourceError>(
                        data = leadContactChannelDistribution,
                    )
                }

            emitAll(leadContactChannelDistributionFlow)
        }

    override fun watchNewStayBookingsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>> =
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

            val newStayBookingsPerMonthFlow = crmMetricsDao
                .watchNewStayBookingsPerMonth(userId = userRemoteId)
                .map { newStayBookingsPerMonth ->
                    val newStayBookingsPerMonthMap = newStayBookingsPerMonth
                        .associate {
                            val split = it.monthYear.split("-")

                            val monthYear = CalendarMonthYear(
                                id = split[0].toInt() + split[1].toInt(),
                                month = split[0].toInt(),
                                year = split[1].toInt(),
                            )

                            monthYear to it.bookingsCount
                        }
                        .toPersistentMap()

                    val newStayBookingsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { 0 }
                        .toMutableMap()

                    newStayBookingsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val bookingsCount = newStayBookingsPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap.put(monthYear, bookingsCount ?: 0)
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>(
                        data = newStayBookingsWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerMonthFlow)
        }

    override fun watchNewStayBookingsPerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>> =
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

            val newStayBookingsPerTenantSegmentPerMonthFlow = crmMetricsDao
                .watchNewStayBookingsPerTenantSegmentPerMonth(userId = userRemoteId)
                .map { newStayBookingsPerTenantSegmentPerMonth ->
                    val newStayBookingsPerTenantSegmentPerMonthMap =
                        newStayBookingsPerTenantSegmentPerMonth
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
                                    .associate { it.customerSegmentName to it.bookingsCount }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val newStayBookingsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, Int>() }
                        .toMutableMap()

                    newStayBookingsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val bookingsCountPerTenantSegment =
                            newStayBookingsPerTenantSegmentPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap
                            .put(monthYear, bookingsCountPerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerTenantSegmentPerMonthFlow)
        }

    override fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>> =
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

            val newStayBookingsPerPropertyOwnerSegmentPerMonthFlow = crmMetricsDao
                .watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(userId = userRemoteId)
                .map { newStayBookingsPerPropertyOwnerSegmentPerMonth ->
                    val newStayBookingsPerPropertyOwnerSegmentPerMonthMap =
                        newStayBookingsPerPropertyOwnerSegmentPerMonth
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
                                    .associate { it.customerSegmentName to it.bookingsCount }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val newStayBookingsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, Int>() }
                        .toMutableMap()

                    newStayBookingsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val bookingsCountPerTenantSegment =
                            newStayBookingsPerPropertyOwnerSegmentPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap
                            .put(monthYear, bookingsCountPerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerPropertyOwnerSegmentPerMonthFlow)
        }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>> =
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

            val newStayBookingsPerTop3NeighborhoodsPerMonthFlow = crmMetricsDao
                .watchNewStayBookingsPerTop3NeighborhoodsPerMonth(userId = userRemoteId)
                .map { newStayBookingsPerTop3NeighborhoodsPerMonth ->
                    val newStayBookingsPerTop3NeighborhoodsPerMonthMap =
                        newStayBookingsPerTop3NeighborhoodsPerMonth
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
                                    .take(3)
                                    .associate { it.propertyNeighborhood to it.bookingsCount }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val newStayBookingsWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, Int>() }
                        .toMutableMap()

                    newStayBookingsWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val bookingsCountPerTenantSegment =
                            newStayBookingsPerTop3NeighborhoodsPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap
                            .put(monthYear, bookingsCountPerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerTop3NeighborhoodsPerMonthFlow)
        }
}
