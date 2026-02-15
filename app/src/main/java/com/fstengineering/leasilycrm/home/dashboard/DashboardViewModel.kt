package com.fstengineering.leasilycrm.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.models.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val crmMetricsRepository: CrmMetricsRepository,
) : ViewModel() {
    private var isInitialized = false

    // Leads CRM metrics
    private val _generatedLeadsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, Int>>(persistentMapOf())
    val generatedLeadsPerMonth = _generatedLeadsPerMonth.asStateFlow()

    private val _generatedLeadsPerTenantTagPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val generatedLeadsPerTenantTagPerMonth =
        _generatedLeadsPerTenantTagPerMonth.asStateFlow()

    private val _leadContactChannelDistribution =
        MutableStateFlow<ImmutableMap<String?, Double>>(persistentMapOf())
    val leadContactChannelDistribution = _leadContactChannelDistribution.asStateFlow()

    // Stay bookings CRM metrics
    private val _newStayBookingsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, Int>>(persistentMapOf())
    val newStayBookingsPerMonth = _newStayBookingsPerMonth.asStateFlow()

    private val _newStayBookingsPerTenantTagPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val newStayBookingsPerTenantTagPerMonth =
        _newStayBookingsPerTenantTagPerMonth.asStateFlow()

    private val _newStayBookingsPerTop3NeighborhoodsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val newStayBookingsPerTop3NeighborhoodsPerMonth =
        _newStayBookingsPerTop3NeighborhoodsPerMonth.asStateFlow()

    // Commission invoice CRM metrics
    private val _commissionInvoicePerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, AppDecimal>>(persistentMapOf())
    val commissionInvoicePerMonth = _commissionInvoicePerMonth.asStateFlow()

    private val _commissionInvoicePerTenantTagPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>>(
            persistentMapOf()
        )
    val commissionInvoicePerTenantTagPerMonth =
        _commissionInvoicePerTenantTagPerMonth.asStateFlow()

    private val _commissionInvoicePerTop3NeighborhoodsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>>(
            persistentMapOf()
        )
    val commissionInvoicePerTop3NeighborhoodsPerMonth =
        _commissionInvoicePerTop3NeighborhoodsPerMonth.asStateFlow()

    private val _averageTicketPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, AppDecimal>>(persistentMapOf())
    val averageTicketPerMonth = _averageTicketPerMonth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        // Leads CRM metrics
        watchGeneratedLeadsPerMonth()
        watchGeneratedLeadsPerTenantTagPerMonth()
        watchLeadContactChannelDistribution()

        // Stay bookings CRM metrics
        watchNewStayBookingsPerMonth()
        watchNewStayBookingsPerTenantTagPerMonth()
        watchNewStayBookingsPerTop3NeighborhoodsPerMonth()

        // Commission invoice CRM metrics
        watchCommissionInvoicePerMonth()
        watchCommissionInvoicePerTenantTagPerMonth()
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

    private fun watchGeneratedLeadsPerTenantTagPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchGeneratedLeadsPerTenantTagPerMonth().collect { result ->
                result
                    .onSuccess { generatedLeadsPerTenantSegmentPerMonth ->
                        _generatedLeadsPerTenantTagPerMonth.update {
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

    private fun watchNewStayBookingsPerTenantTagPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchNewStayBookingsPerTenantTagPerMonth().collect { result ->
                result
                    .onSuccess { newStayBookingsPerTenantSegmentPerMonth ->
                        _newStayBookingsPerTenantTagPerMonth.update {
                            newStayBookingsPerTenantSegmentPerMonth
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

    private fun watchCommissionInvoicePerTenantTagPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchCommissionInvoicePerTenantTagPerMonth()
                .collect { result ->
                    result
                        .onSuccess { commissionInvoicePerTenantSegmentPerMonth ->
                            _commissionInvoicePerTenantTagPerMonth.update {
                                commissionInvoicePerTenantSegmentPerMonth
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
