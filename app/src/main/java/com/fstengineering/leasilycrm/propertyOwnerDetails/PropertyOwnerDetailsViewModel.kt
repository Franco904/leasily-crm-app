package com.fstengineering.leasilycrm.propertyOwnerDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnerDetailsViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwner = MutableStateFlow(PropertyOwner())
    val propertyOwner = _propertyOwner.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchPropertyOwnerJob: Job? = null

    fun init(propertyOwnerId: Long) {
        if (isInitialized) return

        watchPropertyOwner(propertyOwnerId = propertyOwnerId)

        isInitialized = true
    }

    private fun watchPropertyOwner(propertyOwnerId: Long) {
        _isLoading.update { true }

        watchPropertyOwnerJob = viewModelScope.launch {
            propertyOwnerRepository.watchById(propertyOwnerId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { propertyOwner -> _propertyOwner.update { propertyOwner } }

                _isLoading.update { false }
            }
        }
    }

    fun deletePropertyOwner() {
        val propertyOwnerId = propertyOwner.value.id ?: return

        watchPropertyOwnerJob?.cancel()

        viewModelScope.launch {
            propertyOwnerRepository.delete(id = propertyOwnerId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.PropertyOwnerDeleteSuccess) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data object PropertyOwnerDeleteSuccess : UiEvents
    }
}
