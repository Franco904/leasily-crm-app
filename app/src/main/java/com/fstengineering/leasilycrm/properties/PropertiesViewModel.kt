package com.fstengineering.leasilycrm.properties

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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

        watchProperties()
        watchSearchInput()

        isInitialized = true
    }

    private fun watchProperties() {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
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
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
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

    fun onDeleteProperty(property: Property?) {
        _propertyToDelete.update { property }
    }

    fun deleteProperty() {
        viewModelScope.launch {
            val id = propertyToDelete.value?.id ?: return@launch

            propertyRepository.delete(id)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvents.PropertyDeleteSuccess)

                    onDeleteProperty(property = null)
                }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data object PropertyDeleteSuccess : UiEvents
    }
}
