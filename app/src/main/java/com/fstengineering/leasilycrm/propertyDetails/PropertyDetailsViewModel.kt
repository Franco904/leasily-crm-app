package com.fstengineering.leasilycrm.propertyDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
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

        watchProperty(propertyId = propertyId)

        getPropertyCharacteristics()

        isInitialized = true
    }

    private fun watchProperty(propertyId: Long) {
        _isLoading.update { true }

        watchPropertyJob = viewModelScope.launch {
            propertyRepository.watchById(propertyId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
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
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { characteristics -> _propertyCharacteristics.update { characteristics } }

            _isLoading.update { false }
        }
    }

    fun deleteProperty() {
        val propertyId = property.value.id ?: return

        watchPropertyJob?.cancel()

        viewModelScope.launch {
            propertyRepository.delete(id = propertyId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.PropertyDeleteSuccess) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents

        data object NavigateUp : UiEvents

        data object PropertyDeleteSuccess : UiEvents
    }
}
