package com.fstengineering.leasilycrm.customerSegments.models

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType

data class CustomerSegmentTypeUiState(
    val id: Int? = null,
    val name: String? = null,
) {
    fun toDomain() = CustomerSegmentType(
        id = id,
        name = name,
    )

    companion object {
        fun fromDomain(domain: CustomerSegmentType) = CustomerSegmentTypeUiState(
            id = domain.id,
            name = domain.name,
        )
    }
}
