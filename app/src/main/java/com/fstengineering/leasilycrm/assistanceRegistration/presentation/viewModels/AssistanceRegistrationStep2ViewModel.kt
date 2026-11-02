package com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceSelectableImovelQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
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
    private val assistanceSelectableImovelQueryRepository: AssistanceSelectableImovelQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imoveis =
        MutableStateFlow<ImmutableList<ImovelCardUiState>>(persistentListOf())
    val imoveis = _imoveis.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isInitialized) return

        getImoveis()
        watchSearchInput()

        isInitialized = true
    }

    private fun getImoveis() {
        viewModelScope.launch {
            assistanceSelectableImovelQueryRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess { imoveis ->
                    val uiStates = imoveis
                        .map { it.toUiState() }
                        .toImmutableList()

                    _imoveis.update { uiStates }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    assistanceSelectableImovelQueryRepository.getAll(matching = searchInput)
                        .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingImoveis ->
                            val uiStates = matchingImoveis
                                .map { it.toUiState() }
                                .toImmutableList()

                            _imoveis.update { uiStates }
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
