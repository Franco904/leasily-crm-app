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
                .collect { (result, selectedYear) ->
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

//    private fun getSelectedYearStaysDates(
//        storedImovel: Imovel,
//        selectedStartMonth: Int?,
//        selectedStartYear: Int?,
//        selectedEndMonth: Int?,
//        selectedEndYear: Int?,
//    ): EstadiasStructuredDatesMap {
//        val notDeletedStays = storedImovel.stays.filter { !it.isDeleted }
//
//        val staysStructuredDatesMap = getStaysStructuredDatesUseCase(stays = notDeletedStays)
//            .takeIf { it.isNotEmpty() }
//            ?.also { stayDates ->
//                _monthYearOptions.update { stayDates.keys.toPersistentList() }
//            }
//            .takeIf {
//                selectedStartMonth != null
//                        && selectedStartYear != null
//                        && selectedEndMonth != null
//                        && selectedEndYear != null
//            }
//            ?.filter { (monthYear, _) ->
//                val date = now.withMonthYear(monthYear.month, monthYear.year)
//                val selectedStart = now.withMonthYear(selectedStartMonth!!, selectedStartYear!!)
//                val selectedEnd = now.withMonthYear(selectedEndMonth!!, selectedEndYear!!)
//
//                date.isAfter(selectedStart) && date.isBefore(selectedEnd)
//            }
//            ?: return persistentMapOf()
//
//        val staysStructuredDatesWithMissingMonthsMap =
//            AppDate.Companion.getDatesWithMissingMonthsBetween(
//                startDate = now.withMonthYear(selectedStartMonth!!, selectedStartYear!!),
//                endDate = now.withMonthYear(selectedEndMonth!!, selectedEndYear!!),
//                staysStructuredDatesMap = staysStructuredDatesMap,
//            )
//
//        return staysStructuredDatesWithMissingMonthsMap
//    }
//
//    fun getDatesWithMissingMonthsBetween(
//        startDate: AppDate,
//        endDate: AppDate,
//        staysStructuredDatesMap: Map<CalendarMonthYear, PersistentList<EstadiaCalendarDate>>,
//    ): ImmutableMap<CalendarMonthYear, PersistentList<EstadiaCalendarDate>> {
//        val staysStructuredDatesWithMissingMonthsMap =
//            mutableMapOf<CalendarMonthYear, PersistentList<EstadiaCalendarDate>>()
//
//        var currentDate = startDate.date()
//        while (currentDate <= endDate.date()) {
//            val calendarMonthYear = CalendarMonthYear(
//                month = currentDate.monthValue,
//                year = currentDate.year,
//            )
//
//            staysStructuredDatesWithMissingMonthsMap.put(calendarMonthYear, persistentListOf())
//            currentDate = currentDate.plusMonths(1)
//        }
//
//        staysStructuredDatesWithMissingMonthsMap.keys.forEach { monthYear ->
//            val stayDates = staysStructuredDatesMap[monthYear]
//
//            staysStructuredDatesWithMissingMonthsMap.put(
//                monthYear,
//                stayDates ?: persistentListOf()
//            )
//        }
//
//        return staysStructuredDatesWithMissingMonthsMap
//            .toPersistentMap()
//    }

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