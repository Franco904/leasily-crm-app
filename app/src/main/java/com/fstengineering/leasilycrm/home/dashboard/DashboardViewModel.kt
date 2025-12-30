package com.fstengineering.leasilycrm.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DashboardViewModel(
    private val crmMetricsRepository: CrmMetricsRepository,
) : ViewModel() {
    private var isInitialized = false

    // Leads CRM metrics
    private val _generatedLeadsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, Int>>(persistentMapOf())
    val generatedLeadsPerMonth = _generatedLeadsPerMonth.asStateFlow()

    private val _generatedLeadsPerTenantSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val generatedLeadsPerTenantSegmentPerMonth =
        _generatedLeadsPerTenantSegmentPerMonth.asStateFlow()

    private val _leadContactChannelDistribution =
        MutableStateFlow<ImmutableMap<String?, Double>>(persistentMapOf())
    val leadContactChannelDistribution = _leadContactChannelDistribution.asStateFlow()

    // Stay bookings CRM metrics
    private val _newStayBookingsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, Int>>(persistentMapOf())
    val newStayBookingsPerMonth = _newStayBookingsPerMonth.asStateFlow()

    private val _newStayBookingsPerTenantSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val newStayBookingsPerTenantSegmentPerMonth =
        _newStayBookingsPerTenantSegmentPerMonth.asStateFlow()

    private val _newStayBookingsPerPropertyOwnerSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val newStayBookingsPerPropertyOwnerSegmentPerMonth =
        _newStayBookingsPerPropertyOwnerSegmentPerMonth.asStateFlow()

    private val _newStayBookingsPerTop3NeighborhoodsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val newStayBookingsPerTop3NeighborhoodsPerMonth =
        _newStayBookingsPerTop3NeighborhoodsPerMonth.asStateFlow()

    // Commission invoice CRM metrics
    private val _commissionInvoicePerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, BigDecimal>>(persistentMapOf())
    val commissionInvoicePerMonth = _commissionInvoicePerMonth.asStateFlow()

    private val _commissionInvoicePerTenantSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>>(
            persistentMapOf()
        )
    val commissionInvoicePerTenantSegmentPerMonth =
        _commissionInvoicePerTenantSegmentPerMonth.asStateFlow()

    private val _commissionInvoicePerPropertyOwnerSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>>(
            persistentMapOf()
        )
    val commissionInvoicePerPropertyOwnerSegmentPerMonth =
        _commissionInvoicePerPropertyOwnerSegmentPerMonth.asStateFlow()

    private val _commissionInvoicePerTop3NeighborhoodsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>>(
            persistentMapOf()
        )
    val commissionInvoicePerTop3NeighborhoodsPerMonth =
        _commissionInvoicePerTop3NeighborhoodsPerMonth.asStateFlow()

    private val _averageTicketPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, BigDecimal>>(persistentMapOf())
    val averageTicketPerMonth = _averageTicketPerMonth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        // Leads CRM metrics
        watchGeneratedLeadsPerMonth()
        watchGeneratedLeadsPerTenantSegmentPerMonth()
        watchLeadContactChannelDistribution()

        // Stay bookings CRM metrics
        watchNewStayBookingsPerMonth()
        watchNewStayBookingsPerTenantSegmentPerMonth()
        watchNewStayBookingsPerPropertyOwnerSegmentPerMonth()
        watchNewStayBookingsPerTop3NeighborhoodsPerMonth()

        // Commission invoice CRM metrics
        watchCommissionInvoicePerMonth()
        watchCommissionInvoicePerTenantSegmentPerMonth()
        watchCommissionInvoicePerPropertyOwnerSegmentPerMonth()
        watchCommissionInvoicePerTop3NeighborhoodsPerMonth()
        watchAverageTicketPerMonth()

        isInitialized = true
    }

    fun onPause() {
        isInitialized = false
    }

    private fun watchGeneratedLeadsPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchGeneratedLeadsPerMonth().collect { result ->
                result
                    .onSuccess { generatedLeadsPerMonth ->
                        _generatedLeadsPerMonth.update { generatedLeadsPerMonth }
                    }
            }
        }
    }

    private fun watchGeneratedLeadsPerTenantSegmentPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchGeneratedLeadsPerTenantSegmentPerMonth().collect { result ->
                result
                    .onSuccess { generatedLeadsPerTenantSegmentPerMonth ->
                        _generatedLeadsPerTenantSegmentPerMonth.update {
                            generatedLeadsPerTenantSegmentPerMonth
                        }
                    }
            }
        }
    }

    private fun watchLeadContactChannelDistribution() {
        viewModelScope.launch {
            crmMetricsRepository.watchLeadContactChannelDistribution().collect { result ->
                result
                    .onSuccess { leadContactChannelDistribution ->
                        _leadContactChannelDistribution.update { leadContactChannelDistribution }
                    }
            }
        }
    }

    private fun watchNewStayBookingsPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchNewStayBookingsPerMonth().collect { result ->
                result
                    .onSuccess { newStayBookingsPerMonth ->
                        _newStayBookingsPerMonth.update { newStayBookingsPerMonth }
                    }
            }
        }
    }

    private fun watchNewStayBookingsPerTenantSegmentPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchNewStayBookingsPerTenantSegmentPerMonth().collect { result ->
                result
                    .onSuccess { newStayBookingsPerTenantSegmentPerMonth ->
                        _newStayBookingsPerTenantSegmentPerMonth.update {
                            newStayBookingsPerTenantSegmentPerMonth
                        }
                    }
            }
        }
    }

    private fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchNewStayBookingsPerPropertyOwnerSegmentPerMonth()
                .collect { result ->
                    result
                        .onSuccess { newStayBookingsPerPropertyOwnerSegmentPerMonth ->
                            _newStayBookingsPerPropertyOwnerSegmentPerMonth.update {
                                newStayBookingsPerPropertyOwnerSegmentPerMonth
                            }
                        }
                }
        }
    }

    private fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchNewStayBookingsPerTop3NeighborhoodsPerMonth()
                .collect { result ->
                    result
                        .onSuccess { newStayBookingsPerTop3NeighborhoodsPerMonth ->
                            _newStayBookingsPerTop3NeighborhoodsPerMonth.update {
                                newStayBookingsPerTop3NeighborhoodsPerMonth
                            }
                        }
                }
        }
    }

    private fun watchCommissionInvoicePerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchCommissionInvoicePerMonth().collect { result ->
                result
                    .onSuccess { commissionInvoicePerMonth ->
                        _commissionInvoicePerMonth.update { commissionInvoicePerMonth }
                    }
            }
        }
    }

    private fun watchCommissionInvoicePerTenantSegmentPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchCommissionInvoicePerTenantSegmentPerMonth()
                .collect { result ->
                    result
                        .onSuccess { commissionInvoicePerTenantSegmentPerMonth ->
                            _commissionInvoicePerTenantSegmentPerMonth.update {
                                commissionInvoicePerTenantSegmentPerMonth
                            }
                        }
                }
        }
    }

    private fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchCommissionInvoicePerPropertyOwnerSegmentPerMonth()
                .collect { result ->
                    result
                        .onSuccess { commissionInvoicePerPropertyOwnerSegmentPerMonth ->
                            _commissionInvoicePerPropertyOwnerSegmentPerMonth.update {
                                commissionInvoicePerPropertyOwnerSegmentPerMonth
                            }
                        }
                }
        }
    }

    private fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchCommissionInvoicePerTop3NeighborhoodsPerMonth()
                .collect { result ->
                    result
                        .onSuccess { commissionInvoicePerTop3NeighborhoodsPerMonth ->
                            _commissionInvoicePerTop3NeighborhoodsPerMonth.update {
                                commissionInvoicePerTop3NeighborhoodsPerMonth
                            }
                        }
                }
        }
    }

    private fun watchAverageTicketPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchAverageTicketPerMonth().collect { result ->
                result
                    .onSuccess { averageTicketPerMonth ->
                        _averageTicketPerMonth.update { averageTicketPerMonth }
                    }
            }
        }
    }
}
