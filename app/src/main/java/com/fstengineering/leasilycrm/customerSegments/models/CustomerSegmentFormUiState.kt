package com.fstengineering.leasilycrm.customerSegments.models

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment

data class CustomerSegmentFormUiState(
    val id: Int? = null,
    val name: String? = null,
    val type: CustomerSegmentTypeUiState? = null,
    val userId: Int? = null,
) {
    fun toDomain() = CustomerSegment(
        id = id,
        name = name,
        isDeleted = false,
        type = type?.toDomain(),
        userId = userId,
    )

    companion object {
        fun fromDomain(domain: CustomerSegment) = CustomerSegmentFormUiState(
            id = domain.id,
            name = domain.name,
            type = domain.type?.let { CustomerSegmentTypeUiState.fromDomain(domain.type) },
            userId = domain.userId,
        )
    }
}
