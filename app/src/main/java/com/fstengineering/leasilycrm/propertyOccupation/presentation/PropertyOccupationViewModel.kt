package com.fstengineering.leasilycrm.propertyOccupation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateProvider
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.propertyOccupation.application.models.queries.PropertyOccupationArgs
import com.fstengineering.leasilycrm.propertyOccupation.application.useCases.WatchPropertyOccupationUseCase
import com.fstengineering.leasilycrm.propertyOccupation.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyOccupation.presentation.models.PropertyOccupiedPeriodsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOccupationViewModel(
    private val watchPropertyOccupation: WatchPropertyOccupationUseCase,
    dateProvider: AppDateProvider,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _occupiedPeriods =
        MutableStateFlow<PropertyOccupiedPeriodsUiState>(PropertyOccupiedPeriodsUiState.Loading)
    val occupiedPeriods = _occupiedPeriods.asStateFlow()

    private val _selectedYear = MutableStateFlow(dateProvider.now().year)
    val selectedYear = _selectedYear.asStateFlow()

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchPropertyOccupation(propertyId = propertyId)

        isInitialized = true
    }

    private fun watchPropertyOccupation(propertyId: Long) {
        viewModelScope.launch {
            watchPropertyOccupation.invoke(PropertyOccupationArgs(propertyId))
                .combine(selectedYear) { occupiedPeriodsResult, selectedYear ->
                    occupiedPeriodsResult to selectedYear
                }
                .collect { (result, selectedYear) ->
                    result
                        .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                        .onSuccess { storedOccupiedPeriods ->
                            if (storedOccupiedPeriods != occupiedPeriods) {
                                _occupiedPeriods.update { storedOccupiedPeriods.toUiState() }
                            }

                            _occupiedPeriods.update {
                                val loaded = it as? PropertyOccupiedPeriodsUiState.Loaded

                                loaded?.setPeriodsSelection(selectedYear)
                                    ?: PropertyOccupiedPeriodsUiState.Loading
                            }
                        }
                }
        }
    }

//    private fun getSelectedYearStaysDates(
//        storedProperty: Property,
//        selectedStartMonth: Int?,
//        selectedStartYear: Int?,
//        selectedEndMonth: Int?,
//        selectedEndYear: Int?,
//    ): StaysStructuredDatesMap {
//        val notDeletedStays = storedProperty.stays.filter { !it.isDeleted }
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
//        staysStructuredDatesMap: Map<CalendarMonthYear, PersistentList<StayCalendarDate>>,
//    ): ImmutableMap<CalendarMonthYear, PersistentList<StayCalendarDate>> {
//        val staysStructuredDatesWithMissingMonthsMap =
//            mutableMapOf<CalendarMonthYear, PersistentList<StayCalendarDate>>()
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
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object NavigateUp : UiEvent
    }
}