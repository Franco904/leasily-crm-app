package com.fstengineering.leasilycrm.propertyOwners.models

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner

data class PropertyOwnerUiState(
    val id: Int? = null,
    val name: String? = null,
) {
    companion object {
        fun fromDomain(domain: PropertyOwner) = PropertyOwnerUiState(
            id = domain.id,
            name = domain.name,
        )
    }
}
