package com.fstengineering.leasilycrm.core.infra.adapters

import android.util.Log
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.MonitoringAdapter
import kotlinx.collections.immutable.ImmutableList

class MonitoringAdapterImpl : MonitoringAdapter {
    override fun logDomainErrors(tag: String, errors: ImmutableList<DomainError>) {
        log(tag, "Domain errors: $errors")
    }

    private fun log(tag: String, message: String) {
        if (BuildConfig.DEBUG) Log.d(tag, message)
    }
}
