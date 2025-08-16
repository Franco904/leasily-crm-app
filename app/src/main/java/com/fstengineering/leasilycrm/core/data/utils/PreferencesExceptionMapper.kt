package com.fstengineering.leasilycrm.core.data.utils

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.serialization.SerializationException
import java.io.IOException

fun Exception.toPreferencesError() = when (this) {
    is IOException -> DataSourceError.PreferencesError.SerializationError
    is SerializationException -> DataSourceError.PreferencesError.SerializationError
    else -> DataSourceError.PreferencesError.UnknownError
}
