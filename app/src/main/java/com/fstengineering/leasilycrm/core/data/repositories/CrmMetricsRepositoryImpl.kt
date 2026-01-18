package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.application.models.AppDate
import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import com.fstengineering.leasilycrm.core.application.models.toDecimal
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CrmMetricsDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.math.roundToInt

class CrmMetricsRepositoryImpl(
    private val databaseDataSource: CrmMetricsDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
    private val now: AppDate,
) : CrmMetricsRepository {
    override fun watchGeneratedLeadsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val generatedLeadsPerMonthFlow = databaseDataSource
                .watchGeneratedLeadsPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val generatedLeadsPerMonthFlowMapped =
                generatedLeadsPerMonthFlow.map { generatedLeadsPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, Int>, DataError>(
                        data = generatedLeadsWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(generatedLeadsPerMonthFlowMapped)
        }

    override fun watchGeneratedLeadsPerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val generatedLeadsPerTenantSegmentPerMonthFlow = databaseDataSource
                .watchGeneratedLeadsPerTenantSegmentPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val generatedLeadsPerTenantSegmentPerMonthFlowMapped =
                generatedLeadsPerTenantSegmentPerMonthFlow.map { generatedLeadsPerTenantSegmentPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>(
                        data = generatedLeadsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(generatedLeadsPerTenantSegmentPerMonthFlowMapped)
        }

    override fun watchLeadContactChannelDistribution(): Flow<AppResult<ImmutableMap<String?, Double>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val leadContactChannelDistributionFlow = databaseDataSource
                .watchCountLeadsByContactChannelType(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val leadContactChannelDistributionFlowMapped =
                leadContactChannelDistributionFlow.map { leadContactChannelCounts ->
                    val totalLeads = leadContactChannelCounts.sumOf { it.leadCount }

                    val leadContactChannelDistribution = leadContactChannelCounts
                        .sortedBy { it.leadContactChannelName }
                        .associate {
                            val roundedLeadsCount = (it.leadCount.toDouble() / totalLeads) * 100
                            it.leadContactChannelName to roundedLeadsCount.roundToInt().toDouble()
                        }
                        .filterValues { it > 0.0 }
                        .toPersistentMap()

                    AppResult.Success<ImmutableMap<String?, Double>, DataError>(
                        data = leadContactChannelDistribution,
                    )
                }

            emitAll(leadContactChannelDistributionFlowMapped)
        }

    override fun watchNewStayBookingsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val newStayBookingsPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val newStayBookingsPerMonthFlowMapped =
                newStayBookingsPerMonthFlow.map { newStayBookingsPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, Int>, DataError>(
                        data = newStayBookingsWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerMonthFlowMapped)
        }

    override fun watchNewStayBookingsPerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val newStayBookingsPerTenantSegmentPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerTenantSegmentPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val newStayBookingsPerTenantSegmentPerMonthFlowMapped =
                newStayBookingsPerTenantSegmentPerMonthFlow.map { newStayBookingsPerTenantSegmentPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerTenantSegmentPerMonthFlowMapped)
        }

    override fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val newStayBookingsPerPropertyOwnerSegmentPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val newStayBookingsPerPropertyOwnerSegmentPerMonthFlowMapped =
                newStayBookingsPerPropertyOwnerSegmentPerMonthFlow.map { newStayBookingsPerPropertyOwnerSegmentPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerPropertyOwnerSegmentPerMonthFlowMapped)
        }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val newStayBookingsPerTop3NeighborhoodsPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerTop3NeighborhoodsPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val newStayBookingsPerTop3NeighborhoodsPerMonthFlowMapped =
                newStayBookingsPerTop3NeighborhoodsPerMonthFlow.map { newStayBookingsPerTop3NeighborhoodsPerMonth ->
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>(
                        data = newStayBookingsWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(newStayBookingsPerTop3NeighborhoodsPerMonthFlowMapped)
        }

    override fun watchCommissionInvoicePerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val commissionInvoicePerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val commissionInvoicePerMonthFlowMapped =
                commissionInvoicePerMonthFlow.map { commissionInvoicePerMonth ->
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
                        .associateWith { 0.toDecimal() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoice = commissionInvoicePerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap.put(
                            monthYear,
                            commissionInvoice ?: 0.toDecimal()
                        )
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>(
                        data = commissionInvoiceWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerMonthFlowMapped)
        }

    override fun watchCommissionInvoicePerTenantSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val commissionInvoicePerTenantSegmentPerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerTenantSegmentPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val commissionInvoicePerTenantSegmentPerMonthFlowMapped =
                commissionInvoicePerTenantSegmentPerMonthFlow.map { commissionInvoicePerTenantSegmentPerMonth ->
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
                        .associateWith { persistentMapOf<String, AppDecimal>() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoicePerTenantSegment =
                            commissionInvoicePerTenantSegmentPerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap
                            .put(monthYear, commissionInvoicePerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerTenantSegmentPerMonthFlowMapped)
        }

    override fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val commissionInvoicePerPropertyOwnerSegmentPerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val commissionInvoicePerPropertyOwnerSegmentPerMonthFlowMapped =
                commissionInvoicePerPropertyOwnerSegmentPerMonthFlow.map { commissionInvoicePerPropertyOwnerSegmentPerMonth ->
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
                        .associateWith { persistentMapOf<String, AppDecimal>() }
                        .toMutableMap()

                    commissionInvoiceWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val commissionInvoicePerTenantSegment =
                            commissionInvoicePerPropertyOwnerSegmentPerMonthMap[monthYear]
                        commissionInvoiceWithMissingMonthsMap
                            .put(monthYear, commissionInvoicePerTenantSegment ?: persistentMapOf())
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerPropertyOwnerSegmentPerMonthFlowMapped)
        }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val commissionInvoicePerTop3NeighborhoodsPerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerTop3NeighborhoodsPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val commissionInvoicePerTop3NeighborhoodsPerMonthFlowMapped =
                commissionInvoicePerTop3NeighborhoodsPerMonthFlow.map { commissionInvoicePerTop3NeighborhoodsPerMonth ->
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
                        .associateWith { persistentMapOf<String, AppDecimal>() }
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

                    AppResult.Success<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>(
                        data = commissionInvoiceWithMissingMonthsMap
                            .toPersistentMap(),
                    )
                }

            emitAll(commissionInvoicePerTop3NeighborhoodsPerMonthFlowMapped)
        }

    override fun watchAverageTicketPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val averageTicketPerMonthFlow = databaseDataSource
                .watchAverageTicketPerMonth(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val averageTicketPerMonthFlowMapped =
                averageTicketPerMonthFlow.map { averageTicketPerMonth ->
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
                        .associateWith { 0.toDecimal() }
                        .toMutableMap()

                    averageTicketWithMissingMonthsMap.entries.forEach { (monthYear, _) ->
                        val averageTicket = averageTicketPerMonthMap[monthYear]
                        averageTicketWithMissingMonthsMap.put(
                            monthYear,
                            averageTicket ?: 0.toDecimal()
                        )
                    }

                    AppResult.Success<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>(
                        data = averageTicketWithMissingMonthsMap.toPersistentMap(),
                    )
                }

            emitAll(averageTicketPerMonthFlowMapped)
        }
}
