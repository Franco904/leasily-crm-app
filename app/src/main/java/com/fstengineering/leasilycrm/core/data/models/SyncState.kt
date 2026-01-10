package com.fstengineering.leasilycrm.core.data.models

enum class SyncState(val id: Int) {
    Synced(id = 0),
    PendingSync(id = 1),
    Conflict(id = 2),
    Rejected(id = 3),
}
