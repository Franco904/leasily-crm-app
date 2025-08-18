package com.fstengineering.leasilycrm.core.data.models

enum class SyncState(
    val id: Int,
) {
    Clean(id = 0),
    Dirty(id = 1),
    Deleting(id = 2);

    companion object {
        fun fromId(id: Int) = SyncState.entries.find { it.id == id }
    }
}
