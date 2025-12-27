package com.fstengineering.leasilycrm.properties

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertiesViewModel(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _properties = MutableStateFlow<ImmutableList<Property>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _propertyToDelete = MutableStateFlow<Property?>(null)
    val propertyToDelete = _propertyToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchProperties()
        watchSearchInput()

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

    private fun watchProperties() {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyRepository.watchAll().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { properties ->
                        _properties.update { properties }
                    }

                _isLoading.update { false }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    _isLoading.update { true }

                    propertyRepository.getAllMatching(matching = searchInput)
                        .onError { error ->
                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        }
                        .onSuccess { matchingProperties ->
                            _properties.update { matchingProperties }
                            _isLoading.update { false }
                        }
                }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    fun onDeleteProperty(
        property: Property?,
    ) {
        _propertyToDelete.update { property }
    }

    fun deleteProperty() {
        viewModelScope.launch {
            val id = propertyToDelete.value?.id ?: return@launch

            propertyRepository.delete(id)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(UiEvents.PropertyDeleteSuccess)

            onDeleteProperty(property = null)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyDeleteSuccess : UiEvents
    }
}
