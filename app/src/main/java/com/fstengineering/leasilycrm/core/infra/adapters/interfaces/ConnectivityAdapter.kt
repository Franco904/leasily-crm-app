package com.fstengineering.leasilycrm.core.infra.adapters.interfaces

interface ConnectivityAdapter {
    fun isDeviceConnectedToInternet(canUseMobile: Boolean): Boolean
}
