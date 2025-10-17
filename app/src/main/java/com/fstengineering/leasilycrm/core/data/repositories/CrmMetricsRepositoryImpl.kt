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
import java.math.BigDecimal
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

    override fun watchLeadContactChannelDistribution(): Flow<AppResult<ImmutableMap<String?, Double>, DataSourceError>> =
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
                        .sortedBy { it.leadContactChannelName }
                        .associate {
                            val roundedLeadsCount = (it.leadCount.toDouble() / totalLeads) * 100
                            it.leadContactChannelName to roundedLeadsCount.roundToInt().toDouble()
                        }
                        .filterValues { it > 0.0 }
                        .toPersistentMap()

                    AppResult.Success<ImmutableMap<String?, Double>, DataSourceError>(
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
                        val bookingsCountPerPropertyOwnerSegment =
                            newStayBookingsPerPropertyOwnerSegmentPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap
                            .put(
                                monthYear,
                                bookingsCountPerPropertyOwnerSegment ?: persistentMapOf()
                            )
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
                        val bookingsCountPerTop3Neighborhoods =
                            newStayBookingsPerTop3NeighborhoodsPerMonthMap[monthYear]
                        newStayBookingsWithMissingMonthsMap
                            .put(monthYear, bookingsCountPerTop3Neighborhoods ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerTop3NeighborhoodsPerMonthFlow)
        }

    override fun watchCommissionInvoicePerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>> =
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

            val commissionInvoicePerMonthFlow = crmMetricsDao
                .watchCommissionInvoicePerMonth(userId = userRemoteId)
                .map { commissionInvoicePerMonth ->
                    val commissionInvoicePerMonthMap = commissionInvoicePerMonth
                        .associate {
                            val split = it.monthYear.split("-")

                            val monthYear = CalendarMonthYear(
                                month = split[0].toInt(),
                                year = split[1].toInt(),
                            )

                            monthYear to it.commissionInvoice
                        }
                        .toPersistentMap()

                    val commissionInvoiceWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { 0.toBigDecimal() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoice = commissionInvoicePerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap.put(
                            monthYear,
                            commissionInvoice ?: 0.toBigDecimal()
                        )
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>(
                        data = commissionInvoiceWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerMonthFlow)
        }

    override fun watchCommissionInvoicePerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>> =
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

            val commissionInvoicePerTenantSegmentPerMonthFlow = crmMetricsDao
                .watchCommissionInvoicePerTenantSegmentPerMonth(userId = userRemoteId)
                .map { commissionInvoicePerTenantSegmentPerMonth ->
                    val commissionInvoicePerTenantSegmentPerMonthMap =
                        commissionInvoicePerTenantSegmentPerMonth
                            .groupBy {
                                val split = it.monthYear.split("-")

                                CalendarMonthYear(
                                    month = split[0].toInt(),
                                    year = split[1].toInt(),
                                )
                            }
                            .mapValues { (_, compounds) ->
                                compounds
                                    .associate { it.customerSegmentName to it.commissionInvoice }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val commissionInvoiceWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, BigDecimal>() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoicePerTenantSegment =
                            commissionInvoicePerTenantSegmentPerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap
                            .put(monthYear, commissionInvoicePerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerTenantSegmentPerMonthFlow)
        }

    override fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>> =
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

            val commissionInvoicePerPropertyOwnerSegmentPerMonthFlow = crmMetricsDao
                .watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(userId = userRemoteId)
                .map { commissionInvoicePerPropertyOwnerSegmentPerMonth ->
                    val commissionInvoicePerPropertyOwnerSegmentPerMonthMap =
                        commissionInvoicePerPropertyOwnerSegmentPerMonth
                            .groupBy {
                                val split = it.monthYear.split("-")

                                CalendarMonthYear(
                                    month = split[0].toInt(),
                                    year = split[1].toInt(),
                                )
                            }
                            .mapValues { (_, compounds) ->
                                compounds
                                    .associate { it.customerSegmentName to it.commissionInvoice }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val commissionInvoiceWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, BigDecimal>() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoicePerTenantSegment =
                            commissionInvoicePerPropertyOwnerSegmentPerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap
                            .put(monthYear, commissionInvoicePerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerPropertyOwnerSegmentPerMonthFlow)
        }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>> =
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

            val commissionInvoicePerTop3NeighborhoodsPerMonthFlow = crmMetricsDao
                .watchCommissionInvoicePerTop3NeighborhoodsPerMonth(userId = userRemoteId)
                .map { commissionInvoicePerTop3NeighborhoodsPerMonth ->
                    val commissionInvoicePerTop3NeighborhoodsPerMonthMap =
                        commissionInvoicePerTop3NeighborhoodsPerMonth
                            .groupBy {
                                val split = it.monthYear.split("-")

                                CalendarMonthYear(
                                    month = split[0].toInt(),
                                    year = split[1].toInt(),
                                )
                            }
                            .mapValues { (_, compounds) ->
                                compounds
                                    .take(3)
                                    .associate { it.propertyNeighborhood to it.commissionInvoice }
                                    .toPersistentMap()
                            }
                            .toPersistentMap()

                    val commissionInvoiceWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { persistentMapOf<String, BigDecimal>() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoicePerTop3Neighborhoods =
                            commissionInvoicePerTop3NeighborhoodsPerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap
                            .put(
                                monthYear,
                                commissionInvoicePerTop3Neighborhoods ?: persistentMapOf()
                            )
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerTop3NeighborhoodsPerMonthFlow)
        }

    override fun watchAverageTicketPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>> =
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

            val averageTicketPerMonthFlow = crmMetricsDao
                .watchAverageTicketPerMonth(userId = userRemoteId)
                .map { averageTicketPerMonth ->
                    val averageTicketPerMonthMap = averageTicketPerMonth
                        .associate {
                            val split = it.monthYear.split("-")

                            val monthYear = CalendarMonthYear(
                                month = split[0].toInt(),
                                year = split[1].toInt(),
                            )

                            monthYear to it.averageTicket
                        }
                        .toPersistentMap()

                    val averageTicketWithMissingMonthsMap = now.getLast12Months()
                        .associateWith { 0.toBigDecimal() }
                        .toMutableMap()

                    averageTicketWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val averageTicket = averageTicketPerMonthMap[monthYear]
                        averageTicketWithMissingMonthsMap.put(
                            monthYear,
                            averageTicket ?: 0.toBigDecimal()
                        )
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>(
                        data = averageTicketWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(averageTicketPerMonthFlow)
        }
}
