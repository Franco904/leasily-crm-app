package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.helpers.toDecimal
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.CrmMetricsDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
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
            val generatedLeadsPerMonthFlow = databaseDataSource
                .watchGeneratedLeadsPerMonth()
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

    override fun watchGeneratedLeadsPerTenantTagPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val generatedLeadsPerTenantSegmentPerMonthFlow = databaseDataSource
                .watchGeneratedLeadsPerTenantTagPerMonth()
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
                                    .associate { it.tenantTagName to it.leadCount }
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
            val leadContactChannelDistributionFlow = databaseDataSource
                .watchCountLeadsByContactChannelType()
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
            val newStayBookingsPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerMonth()
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

    override fun watchNewStayBookingsPerTenantTagPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val newStayBookingsPerTenantSegmentPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerTenantTagPerMonth()
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
                                    .associate { it.tenantTagName to it.bookingsCount }
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

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>> =
        flow {
            val newStayBookingsPerTop3NeighborhoodsPerMonthFlow = databaseDataSource
                .watchNewStayBookingsPerTop3NeighborhoodsPerMonth()
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
            val commissionInvoicePerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerMonth()
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

    override fun watchCommissionInvoicePerTenantTagPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>> =
        flow {
            val commissionInvoicePerTenantSegmentPerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerTenantTagPerMonth()
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
                                    .associate { it.tenantTagName to it.commissionInvoice }
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

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>> =
        flow {
            val commissionInvoicePerTop3NeighborhoodsPerMonthFlow = databaseDataSource
                .watchCommissionInvoicePerTop3NeighborhoodsPerMonth()
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
            val averageTicketPerMonthFlow = databaseDataSource
                .watchAverageTicketPerMonth()
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
