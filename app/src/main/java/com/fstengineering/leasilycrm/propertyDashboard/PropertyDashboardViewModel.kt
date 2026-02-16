package com.fstengineering.leasilycrm.propertyDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.helpers.StayCalendarDate
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyDashboardViewModel(
    private val getStaysStructuredDatesUseCase: GetStaysStructuredDatesUseCase,
    private val watchPropertyWithStaysTenantsUseCase: WatchPropertyWithStaysTenantsUseCase,
    private val propertyRepository: PropertyRepository,
    private val now: AppDate,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow(Property())
    val property = _property.asStateFlow()

    private val _currentMonthStaysDates =
        MutableStateFlow<Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>>>(
            CalendarMonthYear() to persistentListOf()
        )
    val currentMonthStaysDates = _currentMonthStaysDates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchProperty(propertyId = propertyId)

        isInitialized = true
    }

    private fun watchProperty(propertyId: Long) {
        _isLoading.update { true }

        viewModelScope.launch {
            watchPropertyWithStaysTenantsUseCase(propertyId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { storedProperty ->
                        if (storedProperty == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            return@onSuccess
                        }

                        _property.update { storedProperty }
                        _currentMonthStaysDates.update { getCurrentMonthStaysDates(storedProperty) }
                    }

                _isLoading.update { false }
            }
        }
    }

    private fun getCurrentMonthStaysDates(
        storedProperty: Property,
    ): Pair<CalendarMonthYear, ImmutableList<StayCalendarDate>> {
        val now = now.date()
        val currentMonthYear = CalendarMonthYear(
            month = now.monthValue,
            year = now.year,
        )

        val notDeletedStays = storedProperty.stays.filter { !it.isDeleted }

        val staysStructuredDatesMap = getStaysStructuredDatesUseCase(stays = notDeletedStays)
            .takeUnless { it.isEmpty() }
            ?: persistentMapOf(currentMonthYear to persistentListOf())

        return staysStructuredDatesMap
            .filter { (monthYear, _) -> monthYear == currentMonthYear }
            .entries
            .first()
            .toPair()
    }

    fun deleteProperty() {
        val propertyId = property.value.id ?: return

        viewModelScope.launch {
            propertyRepository.delete(id = propertyId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.PropertyDeleteSuccess) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data object NavigateUp : UiEvents
        data object PropertyDeleteSuccess : UiEvents
    }
}
