package com.fstengineering.leasilycrm.core.domain.models.enums

enum class TenantStatus(val id: Int) {
    Lead(id = 0),
    UpcomingStay(id = 1),
    Old(id = 2);

    companion object {
        fun fromId(id: Int) = entries.find { it.id == id } ?: Lead
    }
}
