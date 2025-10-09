package com.fstengineering.leasilycrm.propertyStays

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
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
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow(Property())
    val property = _property.asStateFlow()

    private val _selectedYear = MutableStateFlow(0)
    val selectedYear = _selectedYear.asStateFlow()

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
            val staysSelectedYearFlow = propertyRepository.watchPropertyStaysSelectedYear()

            watchPropertyWithStaysTenantsFlow
                .combine(staysSelectedYearFlow) { watchPropertyResult, selectedYear ->
                    watchPropertyResult to selectedYear
                }
                .collect { (watchPropertyResult, selectedYearResult) ->
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

                    val selectedYear = selectedYearResult
                        .fold(
                            onError = { error ->
                                _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                                return@collect
                            },
                            onSuccess = { selectedYear -> selectedYear },
                        )

                    _property.update { storedProperty }
                    _selectedYear.update { selectedYear }

                    val selectedYearStaysDates = getSelectedYearStaysDates(
                        storedProperty = storedProperty,
                        selectedYear = selectedYear,
                    )
                    _selectedYearStaysDates.update { selectedYearStaysDates }

                    _isLoading.update { false }
                }
        }
    }

    private fun getSelectedYearStaysDates(
        storedProperty: Property,
        selectedYear: Int,
    ): StaysStructuredDatesMap {
        val notDeletedStays = storedProperty.stays.filter { !it.isDeleted }

        val staysStructuredDatesMap = getStaysStructuredDatesUseCase(stays = notDeletedStays)
            .takeUnless { it.isEmpty() }
            ?.filter { (monthYear, _) -> monthYear.year == selectedYear }
            ?: return persistentMapOf()

        val datesWithMissingMonths =
            mutableMapOf<CalendarMonthYear, PersistentList<StayCalendarDate>>()

        val months = List(12) { i ->
            CalendarMonthYear(id = (i + 1) + selectedYear, month = i + 1, year = selectedYear)
        }

        months.forEach { monthYear ->
            val stayDates = staysStructuredDatesMap[monthYear]
            datesWithMissingMonths.put(monthYear, stayDates ?: persistentListOf())
        }

        return datesWithMissingMonths
            .toPersistentMap()
    }

    fun updatePropertyStaysSelectedYear(
        selectedYear: Int,
    ) {
        viewModelScope.launch {
            propertyRepository.updatePropertyStaysSelectedYear(
                selectedYear = selectedYear
            )
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object NavigateUp : UiEvents
    }
}
