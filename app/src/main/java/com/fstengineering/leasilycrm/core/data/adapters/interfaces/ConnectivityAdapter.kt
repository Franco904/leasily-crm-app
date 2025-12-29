package com.fstengineering.leasilycrm.core.data.adapters.interfaces

interface ConnectivityAdapter {
    fun isDeviceConnectedToInternet(canUseMobile: Boolean): Boolean
}
