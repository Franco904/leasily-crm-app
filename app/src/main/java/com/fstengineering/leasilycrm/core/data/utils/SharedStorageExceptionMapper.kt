package com.fstengineering.leasilycrm.core.data.utils

import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.io.files.FileNotFoundException

fun Exception.toSharedStorageError() = when (this) {
    is FileNotFoundException -> DataSourceError.SharedStorageError.NotFound
    is IllegalArgumentException -> DataSourceError.SharedStorageError.InvalidFile
    else -> DataSourceError.SharedStorageError.UnknownError
}
