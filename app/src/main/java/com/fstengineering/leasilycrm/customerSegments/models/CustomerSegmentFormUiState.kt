package com.fstengineering.leasilycrm.customerSegments.models

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.models.User

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
        type = CustomerSegmentType(
            id = type?.id,
            name = type?.name,
        ),
        user = User(remoteId = userId)
    )

    companion object {
        fun fromDomain(domain: CustomerSegment) = CustomerSegmentFormUiState(
            id = domain.id,
            name = domain.name,
            type = CustomerSegmentTypeUiState(
                id = domain.type.id,
                name = domain.type.name,
            ),
            userId = domain.user.remoteId,
        )
    }
}
