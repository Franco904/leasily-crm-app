package com.fstengineering.leasilycrm.propertyRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.entities.PropertyFile
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyRegistrationViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val propertyTypeRepository: PropertyTypeRepository,
    private val propertySeaProximityTypeRepository: PropertySeaProximityTypeRepository,
    private val propertyCharacteristicRepository: PropertyCharacteristicRepository,
) : ViewModel() {
    private var isInitialized = false

    var propertyId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwner>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _propertyTypes =
        MutableStateFlow<ImmutableList<PropertyType>>(persistentListOf())
    val propertyTypes = _propertyTypes.asStateFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _seaProximityTypes =
        MutableStateFlow<ImmutableList<PropertySeaProximityType>>(persistentListOf())
    val seaProximityTypes = _seaProximityTypes.asStateFlow()

    private val _propertyCharacteristics =
        MutableStateFlow<ImmutableList<PropertyCharacteristic>>(persistentListOf())
    val propertyCharacteristics = _propertyCharacteristics.asStateFlow()

    private val _propertyForm = MutableStateFlow(Property())
    val propertyForm = _propertyForm.asStateFlow()

    private var propertyToEdit: Property? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyId: Long?) {
        if (isInitialized) return

        if (propertyId != null) {
            _isLoading.update { true }

            getPropertyToEdit(propertyId)
            this@PropertyRegistrationViewModel.propertyId = propertyId
        }

        watchPropertyOwners()
        getPropertyTypes()
        getPastNeighborhoods()
        getSeaProximityTypes()
        getPropertyCharacteristics()

        isInitialized = true
    }

    private fun getPropertyToEdit(propertyId: Long) {
        viewModelScope.launch {
            propertyRepository.getById(propertyId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedProperty ->
                    _propertyForm.update { storedProperty }
                    propertyToEdit = storedProperty
                }

            _isLoading.update { false }
        }
    }

    private fun watchPropertyOwners() {
        viewModelScope.launch {
            propertyOwnerRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { propertyOwners -> _propertyOwners.update { propertyOwners } }
            }
        }
    }

    private fun getPropertyTypes() {
        viewModelScope.launch {
            propertyTypeRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { propertyTypes -> _propertyTypes.update { propertyTypes } }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            propertyRepository.getAllNeighborHoodNames()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }

            _isLoading.update { false }
        }
    }

    private fun getSeaProximityTypes() {
        viewModelScope.launch {
            propertySeaProximityTypeRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { seaProximityTypes -> _seaProximityTypes.update { seaProximityTypes } }
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

    fun onPropertyFormChange(form: Property) {
        _propertyForm.update { form }
    }

    fun onPropertyFilePick(contentUriString: String) {
        viewModelScope.launch {
            val newFile = PropertyFile(
                uriString = contentUriString,
                isMainPicture = propertyForm.value.files.isEmpty(),
            )

            val newFiles = (propertyForm.value.files + newFile).toPersistentList()
            _propertyForm.update { propertyForm.value.copy(files = newFiles) }
        }
    }

    fun saveProperty() {
        viewModelScope.launch {
            if (propertyToEdit != null && propertyForm.value == propertyToEdit) {
                _uiEvents.send(UiEvents.PropertyEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            propertyRepository.insertOrUpdate(propertyForm.value)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            val saveEvent =
                if (propertyToEdit != null) UiEvents.PropertyEditSuccess(messageId = R.string.edit_property_success_message)
                else UiEvents.PropertyCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    fun onExit() {
        val needConfirmation = when {
            propertyToEdit != null -> propertyToEdit != propertyForm.value
            else -> propertyForm.value.isAnyFormFieldInitialized
        }

        viewModelScope.launch {
            _uiEvents.send(UiEvents.Exit(needConfirmation))
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents

        data object PropertyCreateSuccess : UiEvents

        data class PropertyEditSuccess(val messageId: Int) : UiEvents

        data class Exit(val needConfirmation: Boolean) : UiEvents
    }
}
