package com.fstengineering.leasilycrm.clientes.presentation.models

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus

enum class HospedeFilterType(
    val status: ClienteStatus,
    val label: Int,
) {
    ComEstadia(ClienteStatus.Hospede, R.string.tenants_upcoming_stays_filter_text),
    Antigo(ClienteStatus.HospedeAntigo, R.string.tenants_old_filter_text),
}
