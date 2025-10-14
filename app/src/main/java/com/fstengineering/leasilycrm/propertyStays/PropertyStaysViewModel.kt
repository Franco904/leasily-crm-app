package com.fstengineering.leasilycrm.propertyStays

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.models.AppLazyDate
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyStaysViewModel(
    private val getStaysStructuredDatesUseCase: GetStaysStructuredDatesUseCase,
    private val watchPropertyWithStaysTenantsUseCase: WatchPropertyWithStaysTenantsUseCase,
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
    private val now: AppLazyDate,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow(Property())
    val property = _property.asStateFlow()

    private val _monthYearOptions =
        MutableStateFlow<ImmutableList<CalendarMonthYear>>(persistentListOf())
    val monthYearOptions = _monthYearOptions.asStateFlow()

    private val _selectedPeriod =
        MutableStateFlow<Pair<CalendarMonthYear?, CalendarMonthYear?>>(null to null)
    val selectedPeriod = _selectedPeriod.asStateFlow()

    private val _selectedYearStaysDates =
        MutableStateFlow<StaysStructuredDatesMap>(persistentMapOf())
    val selectedYearStaysDates = _selectedYearStaysDates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchSessionExpiration()
        watchProperty(propertyId = propertyId)

        isInitialized = true
    }

    private fun watchSessionExpiration() {
        viewModelScope.launch {
            userRepository.watchSessionExpiration().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { hasSessionExpired ->
                        if (hasSessionExpired) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }
                    }
            }
        }
    }

    private fun watchProperty(propertyId: Long) {
        viewModelScope.launch {
            val watchPropertyWithStaysTenantsFlow = watchPropertyWithStaysTenantsUseCase(propertyId)
            val staysSelectedPeriodFlow = propertyRepository.watchPropertyStaysSelectedPeriod()

            watchPropertyWithStaysTenantsFlow
                .combine(staysSelectedPeriodFlow) { watchPropertyResult, selectedPeriod ->
                    watchPropertyResult to selectedPeriod
                }
                .collect { (watchPropertyResult, selectedPeriodResult) ->
                    _isLoading.update { true }

                    val storedProperty = watchPropertyResult
                        .fold(
                            onError = { error ->
                                _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                                return@collect
                            },
                            onSuccess = { storedProperty ->
                                if (storedProperty == null) {
                                    _uiEvents.send(UiEvents.NavigateUp)
                                    return@collect
                                }

                                storedProperty
                            },
                        )

                    val (calendarStart, calendarEnd) = selectedPeriodResult
                        .fold(
                            onError = { error ->
                                _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                                return@collect
                            },
                            onSuccess = { selectedYear -> selectedYear },
                        )

                    _property.update { storedProperty }

                    val selectedYearStaysDates = getSelectedYearStaysDates(
                        storedProperty = storedProperty,
                        selectedStartMonth = calendarStart?.month,
                        selectedStartYear = calendarStart?.year,
                        selectedEndMonth = calendarEnd?.month,
                        selectedEndYear = calendarEnd?.year,
                    )

                    _selectedPeriod.update { calendarStart to calendarEnd }
                    _selectedYearStaysDates.update { selectedYearStaysDates }


                    _isLoading.update { false }
                }
        }
    }

    private fun getSelectedYearStaysDates(
        storedProperty: Property,
        selectedStartMonth: Int?,
        selectedStartYear: Int?,
        selectedEndMonth: Int?,
        selectedEndYear: Int?,
    ): StaysStructuredDatesMap {
        val notDeletedStays = storedProperty.stays.filter { !it.isDeleted }

        val staysStructuredDatesMap = getStaysStructuredDatesUseCase(stays = notDeletedStays)
            .takeIf { it.isNotEmpty() }
            ?.also { stayDates ->
                _monthYearOptions.update { stayDates.keys.toPersistentList() }
            }
            .takeIf {
                selectedStartMonth != null
                        && selectedStartYear != null
                        && selectedEndMonth != null
                        && selectedEndYear != null
            }
            ?.filter { (monthYear, _) ->
                val date = now.withMonthYear(monthYear.month, monthYear.year)
                val selectedStart = now.withMonthYear(selectedStartMonth!!, selectedStartYear!!)
                val selectedEnd = now.withMonthYear(selectedEndMonth!!, selectedEndYear!!)

                date.isAfter(selectedStart) && date.isBefore(selectedEnd)
            }
            ?: return persistentMapOf()

        val staysStructuredDatesWithMissingMonthsMap = AppLazyDate.getDatesWithMissingMonthsBetween(
            startDate = now.withMonthYear(selectedStartMonth!!, selectedStartYear!!),
            endDate = now.withMonthYear(selectedEndMonth!!, selectedEndYear!!),
            staysStructuredDatesMap = staysStructuredDatesMap,
        )

        return staysStructuredDatesWithMissingMonthsMap
    }

    fun updatePropertyStaysSelectedYear(
        selectedStartMonth: Int? = null,
        selectedStartYear: Int? = null,
        selectedEndMonth: Int? = null,
        selectedEndYear: Int? = null,
    ) {
        viewModelScope.launch {
            propertyRepository.updatePropertyStaysSelectedPeriod(
                selectedStartMonth = selectedStartMonth,
                selectedStartYear = selectedStartYear,
                selectedEndMonth = selectedEndMonth,
                selectedEndYear = selectedEndYear,
            )
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object NavigateUp : UiEvents
    }
}
