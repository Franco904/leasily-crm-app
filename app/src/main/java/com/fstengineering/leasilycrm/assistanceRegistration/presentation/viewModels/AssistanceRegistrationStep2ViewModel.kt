package com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSelectablePropertyQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers.toCardDetails
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardDetailsUiState
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
    private val assistanceSelectablePropertyQueryRepository: AssistanceSelectablePropertyQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _properties =
        MutableStateFlow<ImmutableList<PropertyCardDetailsUiState>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isInitialized) return

        getProperties()
        watchSearchInput()

        isInitialized = true
    }

    private fun getProperties() {
        viewModelScope.launch {
            assistanceSelectablePropertyQueryRepository.getAll()
                .onApplicationError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess { properties ->
                    val uiStates = properties
                        .map { it.toCardDetails() }
                        .toImmutableList()

                    _properties.update { uiStates }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    assistanceSelectablePropertyQueryRepository.getAll(matching = searchInput)
                        .onApplicationError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingProperties ->
                            val uiStates = matchingProperties
                                .map { it.toCardDetails() }
                                .toImmutableList()

                            _properties.update { uiStates }
                        }
                }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
    }
}
