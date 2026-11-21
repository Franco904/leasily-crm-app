package com.fstengineering.leasilycrm.core.domain.valueObjects

enum class ClienteStatus(val id: Int) {
    Lead(id = 0),
    Hospede(id = 1),
    HospedeAntigo(id = 2);

    companion object {
        fun parse(id: Int) = entries.find { it.id == id } ?: Lead
    }
}