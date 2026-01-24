package com.fstengineering.leasilycrm.core.infra.adapters

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter

class ConnectivityAdapterImpl(
    private val connectivityManager: ConnectivityManager,
) : ConnectivityAdapter {
    override fun isDeviceConnectedToInternet(canUseMobile: Boolean): Boolean {
        return try {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            val hasNonMobileConnection =
                capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true

            if (hasNonMobileConnection) return true

            return canUseMobile &&
                    capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) e.printStackTrace()
            false
        }
    }
}
