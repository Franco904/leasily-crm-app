package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

enum class TenantStatus(val id: Int) {
    Lead(id = 0),
    UpcomingStay(id = 1),
    Old(id = 2);

    companion object {
        fun parse(id: Int) =
            entries.find { it.id == id } ?: throw ParsingError.Tenant.StatusInvalid.asException()
    }
}