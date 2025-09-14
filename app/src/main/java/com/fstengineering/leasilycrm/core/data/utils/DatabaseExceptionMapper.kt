package com.fstengineering.leasilycrm.core.data.utils

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

fun Exception.toDatabaseError() = when (this) {
    is IllegalStateException -> DataSourceError.PreferencesError.NotFound
    else -> DataSourceError.PreferencesError.UnknownError
}
