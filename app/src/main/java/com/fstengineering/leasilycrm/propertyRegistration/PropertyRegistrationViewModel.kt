package com.fstengineering.leasilycrm.propertyRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyCharacteristicPropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyPriceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyRegistrationViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyCharacteristicPropertyRepository: PropertyCharacteristicPropertyRepository,
    private val propertyPriceRepository: PropertyPriceRepository,
    private val propertyFileRepository: PropertyFileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyForm = MutableStateFlow(Property())
    val propertyForm = _propertyForm.asStateFlow()

    var propertyToEdit: Property? = null
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyId: Int?) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (propertyId != null) {
            getPropertyToEdit(propertyId)
        } else {
            _isLoading.update { false }
        }

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

    private fun getPropertyToEdit(propertyId: Int) {
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

    fun onPropertyFormChange(form: Property) {
        _propertyForm.update { form }
    }

//    fun saveProperty() {
//        viewModelScope.launch {
//            if (propertyToEdit != null && propertyForm.value == propertyToEdit) {
//                _uiEvents.send(UiEvents.PropertyEditSuccess(messageId = R.string.snackbar_no_data_changed))
//                return@launch
//            }
//
//            val propertyId = propertyRepository.insertOrUpdate(propertyForm.value)
//                .fold(
//                    onError = { error ->
//                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
//                        return@launch
//                    },
//                    onSuccess = { propertyId -> propertyId },
//                )
//
//            propertyForm.value.characteristics.forEach { characteristic ->
//                propertyCharacteristicPropertyRepository.insertOrUpdate(
//                    characteristicProperty = characteristic.copy(propertyId = propertyId),
//                )
//            }
//
//            propertyForm.value.prices.forEach { price ->
//                propertyPriceRepository.insertOrUpdate(
//                    price = price.copy(propertyId = propertyId),
//                )
//            }
//
//            propertyForm.value.files.forEach { file ->
//                propertyFileRepository.insertOrUpdate(
//                    file = file.copy(propertyId = propertyId),
//                )
//            }
//
//            _uiEvents.send(
//                if (propertyToEdit != null) UiEvents.PropertyEditSuccess(messageId = R.string.snackbar_edit_property_owner_succeeded)
//                else UiEvents.PropertyCreateSuccess,
//            )
//        }
//    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

//        data object PropertyCreateSuccess : UiEvents
//
//        data class PropertyEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
