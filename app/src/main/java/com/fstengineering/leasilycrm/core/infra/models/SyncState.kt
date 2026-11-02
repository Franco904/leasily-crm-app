package com.fstengineering.leasilycrm.core.infra.projections

enum class SyncState(val id: Int) {
    Synced(id = 0),
    PendingSync(id = 1),
    Conflict(id = 2),
    Inconsistency(id = 3),
}
