package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models

enum class OccupiedPeriodField(val scrollIndex: Int) {
    Property(scrollIndex = 0),
    Tenant(scrollIndex = 4),
    Period(scrollIndex = 16),
    CheckInTime(scrollIndex = 18),
    CheckOutTime(scrollIndex = 18),
    Commission(scrollIndex = 24),
}
