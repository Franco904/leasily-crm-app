package com.fstengineering.leasilycrm.home.tenants

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrdenationType
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.home.tenants.models.TenantFilterType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TenantsViewModel(
    private val tenantRepository: TenantRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _tenants = MutableStateFlow<ImmutableList<Tenant>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _tenantToDelete = MutableStateFlow<Tenant?>(null)
    val tenantToDelete = _tenantToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    private val _ordenationType = MutableStateFlow(LeadTenantOrdenationType.Name)
    val ordenationType = _ordenationType.asStateFlow()

    private val _currentTenantFilter = MutableStateFlow(TenantFilterType.UpcomingStay)
    val currentTenantFilter = _currentTenantFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchTenants()
//        watchSearchInput()

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

    private fun watchTenants() {
        _isLoading.update { true }

        viewModelScope.launch {
            val ordenationTypeId = tenantRepository.getPreferredLeadsOrdenationTypeId()
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { ordenationTypeId -> ordenationTypeId },
                )

            _ordenationType.update { LeadTenantOrdenationType.fromId(ordenationTypeId) }

            tenantRepository.watchAllTenants(
                ordenationTypeId = ordenationTypeId,
                tenantFilterType = currentTenantFilter.value.id,
            ).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { tenants ->
                        _tenants.update { tenants }
                    }

                _isLoading.update { false }
            }
        }
    }

    // TODO: Impl. tenant search
//    @OptIn(FlowPreview::class)
//    fun watchSearchInput() {
//        viewModelScope.launch {
//            searchInput
//                .debounce(500.milliseconds)
//                .collect { searchInput ->
//                    _isLoading.update { true }
//
//                    propertyRepository.getAllMatching(matching = searchInput)
//                        .onError { error ->
//                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
//                        }
//                        .onSuccess { matchingProperties ->
//                            _leads.update { matchingProperties }
//                            _isLoading.update { false }
//                        }
//                }
//        }
//    }

    fun onTenantFilterTypeChange(tenantFilterType: TenantFilterType) {
        viewModelScope.launch {
            _currentTenantFilter.update { tenantFilterType }
            watchTenants()
        }
    }

    fun onOrdenationTypeChange(ordenationType: LeadTenantOrdenationType) {
        viewModelScope.launch {
            tenantRepository.updatePreferredLeadsOrdenationTypeId(ordenationType.id)

            watchTenants()
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteTenant(tenant: Tenant?) {
        _tenantToDelete.update { tenant }
    }

    fun deleteTenant() {
        viewModelScope.launch {
            val tenantToDelete = this@TenantsViewModel.tenantToDelete.value ?: return@launch

            tenantRepository.delete(tenantToDelete.id!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(UiEvents.LeadDeleteSuccess)
            onDeleteTenant(tenant = null)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object LeadDeleteSuccess : UiEvents
    }
}
