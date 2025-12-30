package com.fstengineering.leasilycrm.core.application.di.modules

import android.content.Context
import android.net.ConnectivityManager
import com.fstengineering.leasilycrm.core.data.adapters.ConnectivityAdapterImpl
import com.fstengineering.leasilycrm.core.data.adapters.interfaces.ConnectivityAdapter
import org.koin.dsl.module

val connectivityModule = module {
    single<ConnectivityAdapter> {
        val appContext = get<Context>()
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        ConnectivityAdapterImpl(connectivityManager = connectivityManager)
    }
}
