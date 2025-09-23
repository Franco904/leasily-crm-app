package com.fstengineering.leasilycrm.core.domain.models.enums

enum class TenantStatus(val code: Int) {
    Lead(code = 0),
    UpcomingStay(code = 1),
    Old(code = 2);

    companion object {
        fun fromCode(code: Int) = TenantStatus.entries.find { it.code == code }
    }
}
