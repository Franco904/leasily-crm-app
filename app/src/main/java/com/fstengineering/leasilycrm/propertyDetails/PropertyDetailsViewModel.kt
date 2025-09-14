package com.fstengineering.leasilycrm.propertyDetails

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyDetailsViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyCharacteristicRepository: PropertyCharacteristicRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow(Property())
    val property = _property.asStateFlow()

    private val _propertyCharacteristics =
        MutableStateFlow<ImmutableList<PropertyCharacteristic>>(persistentListOf())
    val propertyCharacteristics = _propertyCharacteristics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchPropertyJob: Job? = null

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchSessionExpiration()
        watchProperty(propertyId = propertyId)

        getPropertyCharacteristics()

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

        watchPropertyJob = viewModelScope.launch {
            propertyRepository.watchById(propertyId).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { storedProperty ->
                        if (storedProperty == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            watchPropertyJob?.cancel()
                            return@onSuccess
                        }

                        _property.update { storedProperty }
                    }
            }
        }
    }

    private fun getPropertyCharacteristics() {
        viewModelScope.launch {
            propertyCharacteristicRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedCharacteristics ->
                    _propertyCharacteristics.update { storedCharacteristics }
                }

            _isLoading.update { false }
        }
    }

    fun deleteProperty() {
        val propertyId = property.value.id ?: return

        watchPropertyJob?.cancel()

        viewModelScope.launch {
            propertyRepository.delete(id = propertyId)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
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
