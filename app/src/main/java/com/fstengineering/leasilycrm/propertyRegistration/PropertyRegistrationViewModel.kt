package com.fstengineering.leasilycrm.propertyRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicPropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyPriceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
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
    private val propertyCharacteristicPropertyRepository: PropertyCharacteristicPropertyRepository,
    private val propertyPriceRepository: PropertyPriceRepository,
    private val propertyFileRepository: PropertyFileRepository,
    private val userRepository: UserRepository,
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

    var propertyToEdit: Property? = null
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyId: Long?) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (propertyId != null) {
            getPropertyToEdit(propertyId)
            this.propertyId = propertyId
        } else {
            _isLoading.update { false }
        }

        watchPropertyOwners()
        getPropertyTypes()
        getPastNeighborhoods()
        getSeaProximityTypes()
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

    private fun getPropertyToEdit(propertyId: Long) {
        viewModelScope.launch {
            propertyRepository.getById(propertyId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
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
                    .onError { error ->
                        _uiEvents.send(
                            UiEvents.DataSourceError(
                                messageId = error.toUiMessage()
                            )
                        )
                    }
                    .onSuccess { storedPropertyOwners ->
                        _propertyOwners.update { storedPropertyOwners }
                    }
            }
        }
    }

    private fun getPropertyTypes() {
        viewModelScope.launch {
            propertyTypeRepository.getAll()
                .onError { error ->
                    _uiEvents.send(
                        UiEvents.DataSourceError(
                            messageId = error.toUiMessage()
                        )
                    )
                }
                .onSuccess { storedPropertyTypes ->
                    _propertyTypes.update { storedPropertyTypes }
                }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            propertyRepository.getAllNeighborHoodNames()
                .onError { error ->
                    _uiEvents.send(
                        UiEvents.DataSourceError(
                            messageId = error.toUiMessage()
                        )
                    )
                }
                .onSuccess { neighborhoodNames ->
                    _pastNeighborhoods.update { neighborhoodNames }
                }

            _isLoading.update { false }
        }
    }

    private fun getSeaProximityTypes() {
        viewModelScope.launch {
            propertySeaProximityTypeRepository.getAll()
                .onError { error ->
                    _uiEvents.send(
                        UiEvents.DataSourceError(
                            messageId = error.toUiMessage()
                        )
                    )
                }
                .onSuccess { storedSeaProximityTypes ->
                    _seaProximityTypes.update { storedSeaProximityTypes }
                }
        }
    }

    private fun getPropertyCharacteristics() {
        viewModelScope.launch {
            propertyCharacteristicRepository.getAll()
                .onError { error ->
                    _uiEvents.send(
                        UiEvents.DataSourceError(
                            messageId = error.toUiMessage()
                        )
                    )
                }
                .onSuccess { storedPropertyCharacteristics ->
                    _propertyCharacteristics.update { storedPropertyCharacteristics }
                }

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
                _uiEvents.send(UiEvents.PropertyEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            val propertyId = propertyRepository.insertOrUpdate(propertyForm.value)
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { propertyId -> propertyId },
                )

            propertyForm.value.characteristics.forEach { characteristic ->
                propertyCharacteristicPropertyRepository.insertOrUpdate(
                    characteristicProperty = characteristic.copy(propertyId = propertyId),
                )
            }

            propertyForm.value.prices.forEach { price ->
                propertyPriceRepository.insertOrUpdate(
                    price = price.copy(propertyId = propertyId),
                )
            }

            propertyForm.value.files.forEach { file ->
                propertyFileRepository.insertOrUpdate(
                    file = file.copy(propertyId = propertyId),
                )
            }

            val saveEvent =
                if (propertyToEdit != null) UiEvents.PropertyEditSuccess(messageId = R.string.snackbar_edit_property_succeeded)
                else UiEvents.PropertyCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyCreateSuccess : UiEvents

        data class PropertyEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
