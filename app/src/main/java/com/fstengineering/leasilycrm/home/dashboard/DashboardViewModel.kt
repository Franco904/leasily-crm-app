package com.fstengineering.leasilycrm.home.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.CrmMetricsRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.home.stays.StaysViewModel.UiEvents
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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

    private val _leadContactChannelDistribution =
        MutableStateFlow<ImmutableList<Pair<String, Double>>>(persistentListOf())
    val leadContactChannelDistribution = _leadContactChannelDistribution.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchLeadContactChannelDistribution()

        isInitialized = true
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
