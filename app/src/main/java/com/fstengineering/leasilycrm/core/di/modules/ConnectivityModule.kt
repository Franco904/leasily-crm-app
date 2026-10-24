package com.fstengineering.leasilycrm.core.di.modules

import android.content.Context
import android.net.ConnectivityManager
import com.fstengineering.leasilycrm.core.infra.adapters.ConnectivityAdapter
import org.koin.dsl.module

val connectivityModule = module {
    single {
        val appContext = get<Context>()
        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        ConnectivityAdapter(connectivityManager = connectivityManager)
    }
}
