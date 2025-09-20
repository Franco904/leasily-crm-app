package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.models.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicAssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceCharacteristicRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceInteractionRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSearchedNeighborhoodRepository
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceSentPropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertySeaProximityTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistanceRegistrationViewModel(
    private val assistanceRepository: AssistanceRepository,
    private val tenantRepository: TenantRepository,
    private val assistanceSearchedNeighborhoodRepository: AssistanceSearchedNeighborhoodRepository,
    private val propertySeaProximityTypeRepository: PropertySeaProximityTypeRepository,
    private val assistanceCharacteristicRepository: AssistanceCharacteristicRepository,
    private val assistanceCharacteristicAssistanceRepository: AssistanceCharacteristicAssistanceRepository,
    private val assistanceSentPropertyRepository: AssistanceSentPropertyRepository,
    private val assistanceInteractionRepository: AssistanceInteractionRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    var assistanceId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leadTenantName = MutableStateFlow("")
    val leadTenantName = _leadTenantName.asStateFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _seaProximityTypes =
        MutableStateFlow<ImmutableList<PropertySeaProximityType>>(persistentListOf())
    val seaProximityTypes = _seaProximityTypes.asStateFlow()

    private val _assistancePropertyCharacteristics =
        MutableStateFlow<ImmutableList<AssistanceCharacteristic>>(persistentListOf())
    val assistancePropertyCharacteristics = _assistancePropertyCharacteristics.asStateFlow()

    private val _assistanceForm = MutableStateFlow(Assistance())
    val assistanceForm = _assistanceForm.asStateFlow()

    var assistanceToEdit: Assistance? = null
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(
        assistanceId: Long?,
        leadTenantId: Long,
    ) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (assistanceId != null) {
            getAssistanceToEdit(assistanceId)
            this.assistanceId = assistanceId
        } else {
            _isLoading.update { false }
        }

        getLeadTenantName(leadTenantId = leadTenantId)
        getPastNeighborhoods()
        getSeaProximityTypes()
        getAssistancePropertyCharacteristics()

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

    private fun getAssistanceToEdit(assistanceId: Long) {
        viewModelScope.launch {
            assistanceRepository.getById(assistanceId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedAssistance ->
                    _assistanceForm.update { storedAssistance }
                    assistanceToEdit = storedAssistance
                }

            _isLoading.update { false }
        }
    }

    private fun getLeadTenantName(leadTenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getNameById(tenantId = leadTenantId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedTenantName ->
                    _leadTenantName.update { storedTenantName }
                }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            assistanceRepository.getAllNeighborhoodNames()
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
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedSeaProximityTypes ->
                    _seaProximityTypes.update { storedSeaProximityTypes }
                }
        }
    }

    private fun getAssistancePropertyCharacteristics() {
        viewModelScope.launch {
            assistanceCharacteristicRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedAssistanceCharacteristics ->
                    _assistancePropertyCharacteristics.update { storedAssistanceCharacteristics }
                }

            _isLoading.update { false }
        }
    }

    fun onAssistanceFormChange(form: Assistance) {
        _assistanceForm.update { form }
    }

//    fun onPropertyFilePick(contentUriString: String) {
//        viewModelScope.launch {
//            val newFile = PropertyFile(
//                uriString = contentUriString,
//                isMainPicture = assistanceForm.value.files.isEmpty(),
//            )
//
//            val newFiles = (assistanceForm.value.files + newFile).toPersistentList()
//            _assistanceForm.update { assistanceForm.value.copy(files = newFiles) }
//        }
//    }

    fun saveAssistance() {
        viewModelScope.launch {
            if (assistanceToEdit != null && assistanceForm.value == assistanceToEdit) {
                _uiEvents.send(UiEvents.AssistanceEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            val assistanceId = assistanceRepository.insertOrUpdate(assistanceForm.value)
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { assistanceId -> assistanceId },
                )

            assistanceForm.value.searchedNeighborhoods.forEach { searchedNeighborhood ->
                assistanceSearchedNeighborhoodRepository.insertOrUpdate(
                    searchedNeighborhood = searchedNeighborhood.copy(assistanceId = assistanceId),
                )
            }

            assistanceForm.value.characteristics.forEach { characteristic ->
                assistanceCharacteristicAssistanceRepository.insertOrUpdate(
                    characteristicAssistance = characteristic.copy(assistanceId = assistanceId),
                )
            }

            assistanceForm.value.sentProperties.forEach { sentProperty ->
                assistanceSentPropertyRepository.insertOrUpdate(
                    sentProperty = sentProperty.copy(assistanceId = assistanceId),
                )
            }

            assistanceForm.value.interactions.forEach { interaction ->
                assistanceInteractionRepository.insertOrUpdate(
                    interaction = interaction.copy(assistanceId = assistanceId),
                )
            }

            _uiEvents.send(
                if (assistanceToEdit != null) UiEvents.AssistanceEditSuccess(messageId = R.string.snackbar_edit_property_succeeded)
                else UiEvents.AssistanceCreateSuccess,
            )
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object AssistanceCreateSuccess : UiEvents

        data class AssistanceEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
