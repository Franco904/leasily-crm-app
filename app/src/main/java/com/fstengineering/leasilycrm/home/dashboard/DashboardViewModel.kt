package com.fstengineering.leasilycrm.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.home.stays.StaysViewModel.UiEvents
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val crmMetricsRepository: CrmMetricsRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _generatedLeadsPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, Int>>(persistentMapOf())
    val generatedLeadsPerMonth = _generatedLeadsPerMonth.asStateFlow()

    private val _generatedLeadsPerTenantSegmentPerMonth =
        MutableStateFlow<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>(persistentMapOf())
    val generatedLeadsPerTenantSegmentPerMonth =
        _generatedLeadsPerTenantSegmentPerMonth.asStateFlow()

    private val _leadContactChannelDistribution =
        MutableStateFlow<ImmutableMap<String, Double>>(persistentMapOf())
    val leadContactChannelDistribution = _leadContactChannelDistribution.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchGeneratedLeadsPerMonth()
        watchGeneratedLeadsPerTenantSegmentPerMonth()
        watchLeadContactChannelDistribution()

        isInitialized = true
    }

    private fun watchGeneratedLeadsPerMonth() {
        viewModelScope.launch {
            crmMetricsRepository.watchGeneratedLeadsPerMonth().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
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
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { generatedLeadsPerTenantSegmentPerMonth ->
                        _generatedLeadsPerTenantSegmentPerMonth.update { generatedLeadsPerTenantSegmentPerMonth }
                    }
            }
        }
    }

    private fun watchLeadContactChannelDistribution() {
        viewModelScope.launch {
            crmMetricsRepository.watchLeadContactChannelDistribution().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { leadContactChannelDistribution ->
                        _leadContactChannelDistribution.update { leadContactChannelDistribution }
                    }
            }
        }
    }
}
