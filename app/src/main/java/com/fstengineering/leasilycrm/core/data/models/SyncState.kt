package com.fstengineering.leasilycrm.core.data.models

enum class SyncState(
    val id: Int,
) {
    Clean(id = 0),
    Dirty(id = 1),
    Deleting(id = 2);
}
