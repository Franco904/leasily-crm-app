package com.fstengineering.leasilycrm.assistanceRegistration.viewModels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
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

class AssistanceRegistrationStep2ViewModel(
    private val propertyRepository: PropertyRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _selectableProperties =
        MutableStateFlow<ImmutableList<Property>>(persistentListOf())
    val selectableProperties = _selectableProperties.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        getProperties()
        watchSearchInput()

        isInitialized = true
    }

    private fun getProperties() {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { properties ->
                    _selectableProperties.update { properties }
                }

            _isLoading.update { false }
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
                            _selectableProperties.update { matchingProperties }
                            _isLoading.update { false }
                        }
                }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents
    }
}
