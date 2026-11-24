package com.fstengineering.leasilycrm.core.infra.adapters

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.fstengineering.leasilycrm.core.infra.utils.maskFailure

class ConnectivityAdapter(
    private val connectivityManager: ConnectivityManager,
) {
    /**
     * `true` quando a rede ativa usa Wi-Fi, Ethernet ou celular. Falha na consulta devolve `false`
     * e é registrada em log, sem chegar ao chamador.
     */
    fun isDeviceConnectedToInternet(): Boolean {
        return maskFailure(fallback = false) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
    }
}
