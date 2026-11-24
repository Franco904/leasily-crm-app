package com.fstengineering.leasilycrm.imovelOcupacao.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.queries.ImovelOcupacaoArgs
import com.fstengineering.leasilycrm.imovelOcupacao.application.useCases.WatchImovelOcupacaoUseCase
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelOcupacao.presentation.models.ImovelPeriodosOcupadosUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImovelOcupacaoViewModel(
    private val route: Destinations.ImovelOcupacao,
    private val watchImovelOcupacao: WatchImovelOcupacaoUseCase,
    dateProvider: AppDateProvider,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _occupiedPeriods =
        MutableStateFlow<ImovelPeriodosOcupadosUiState>(ImovelPeriodosOcupadosUiState.Loading)
    val occupiedPeriods = _occupiedPeriods.asStateFlow()

    private val _selectedYear = MutableStateFlow(dateProvider.now().year)
    val selectedYear = _selectedYear.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchImovelOcupacao(imovelId = route.imovelId)
    }

    private fun watchImovelOcupacao(imovelId: Long) {
        viewModelScope.launch {
            watchImovelOcupacao.invoke(ImovelOcupacaoArgs(imovelId))
                .combine(selectedYear) { occupiedPeriodsResult, selectedYear ->
                    occupiedPeriodsResult to selectedYear
                }
                .collectLatest { (result, selectedYear) ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                        .onSuccess { storedOccupiedPeriods ->
                            if (storedOccupiedPeriods != occupiedPeriods) {
                                _occupiedPeriods.update { storedOccupiedPeriods.toUiState() }
                            }

                            _occupiedPeriods.update {
                                val loaded = it as? ImovelPeriodosOcupadosUiState.Loaded

                                loaded?.setPeriodsSelection(selectedYear)
                                    ?: ImovelPeriodosOcupadosUiState.Loading
                            }
                        }
                }
        }
    }

    fun onYearChange(newSelectedYear: Int) {
        _selectedYear.update { newSelectedYear }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
        data object NavigateUp : UiEvent
    }
}
