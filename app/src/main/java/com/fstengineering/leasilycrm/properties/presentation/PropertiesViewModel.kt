package com.fstengineering.leasilycrm.properties.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.properties.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.properties.presentation.mappers.toUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
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
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertySharedCommandRepository: PropertySharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _properties =
        MutableStateFlow<ImmutableList<PropertyCardUiState>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _propertyToDelete = MutableStateFlow<PropertyCardUiState?>(null)
    val propertyToDelete = _propertyToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchProperties()
        watchSearchInput()

        isInitialized = true
    }

    private fun watchProperties() {
        viewModelScope.launch {
            propertyQueryRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedProperties ->
                        val properties = storedProperties.map { it.toUiState() }.toPersistentList()
                        _properties.update { properties }
                    }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    propertyQueryRepository.getAllMatching(matching = searchInput)
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingProperties ->
                            val properties =
                                matchingProperties.map { it.toUiState() }.toPersistentList()
                            _properties.update { properties }
                        }
                }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    fun onDeleteProperty(property: PropertyCardUiState?) {
        _propertyToDelete.update { property }
    }

    fun deleteProperty() {
        viewModelScope.launch {
            val id = propertyToDelete.value?.id ?: return@launch

            propertySharedCommandRepository.delete(id)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.PropertyDeleteSuccess)

                    onDeleteProperty(property = null)
                }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object PropertyDeleteSuccess : UiEvent
    }
}
