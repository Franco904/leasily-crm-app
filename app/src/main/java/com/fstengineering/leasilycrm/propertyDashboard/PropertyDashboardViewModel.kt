package com.fstengineering.leasilycrm.propertyDashboard

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.models.LazyAppDate
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
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
    private val userRepository: UserRepository,
    private val now: LazyAppDate,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow(Property())
    val property = _property.asStateFlow()

    private val _currentMonthStaysDates = MutableStateFlow<StaysStructuredDatesMap>(
        persistentMapOf(CalendarMonthYear() to persistentListOf())
    )
    val currentMonthStaysDates = _currentMonthStaysDates.asStateFlow()

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
        _isLoading.update { true }

        viewModelScope.launch {
            watchPropertyWithStaysTenantsUseCase(propertyId).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
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
    ): StaysStructuredDatesMap {
        val now = now.date()
        val currentMonthYear = CalendarMonthYear(
            id = now.monthValue + now.year,
            month = now.monthValue,
            year = now.year,
        )

        val notDeletedStays = storedProperty.stays.filter { !it.isDeleted }

        val staysStructuredDatesMap = getStaysStructuredDatesUseCase(stays = notDeletedStays)
            .takeUnless { it.isEmpty() }
            ?: persistentMapOf(currentMonthYear to persistentListOf())

        return staysStructuredDatesMap
            .filter { (monthYear, _) -> monthYear == currentMonthYear }
            .toPersistentMap()
    }

    fun deleteProperty() {
        val propertyId = property.value.id ?: return

        viewModelScope.launch {
            propertyRepository.delete(id = propertyId)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.NavigateUp)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.PropertyDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object NavigateUp : UiEvents

        data object PropertyDeleteSuccess : UiEvents
    }
}
