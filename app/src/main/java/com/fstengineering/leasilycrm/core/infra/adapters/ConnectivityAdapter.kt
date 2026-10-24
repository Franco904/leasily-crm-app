package com.fstengineering.leasilycrm.core.infra.adapters

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fstengineering.leasilycrm.BuildConfig

class ConnectivityAdapter(
    private val connectivityManager: ConnectivityManager,
) {
    fun isDeviceConnectedToInternet(): Boolean {
        return try {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            false
        }
    }
}
