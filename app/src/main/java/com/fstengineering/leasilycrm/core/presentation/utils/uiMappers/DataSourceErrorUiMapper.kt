package com.fstengineering.leasilycrm.core.presentation.utils.uiMappers

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

fun DataSourceError.toUiMessage() = when (this) {
    DataSourceError.PreferencesError.NotFound -> {
        R.string.snackbar_preferences_not_found_error
    }

    DataSourceError.PreferencesError.SerializationError -> {
        R.string.snackbar_preferences_serialization_error
    }

    DataSourceError.PreferencesError.SecurityError -> {
        R.string.snackbar_preferences_security_error
    }

    DataSourceError.PreferencesError.UnknownError -> {
        R.string.snackbar_unknown_error
    }

    DataSourceError.RemoteBackendError.BadRequest -> {
        R.string.snackbar_http_bad_request_error
    }

    DataSourceError.RemoteBackendError.Unauthorized -> {
        R.string.snackbar_http_unauthorized_error
    }

    DataSourceError.RemoteBackendError.NotFound -> {
        R.string.snackbar_http_not_found_error
    }

    DataSourceError.RemoteBackendError.ServerError -> {
        R.string.snackbar_http_server_error
    }

    DataSourceError.RemoteBackendError.UnknownHttpRemoteError -> {
        R.string.snackbar_http_unknown_http_response_error
    }

    DataSourceError.RemoteBackendError.SerializationError -> {
        R.string.snackbar_http_serialization_error
    }

    DataSourceError.RemoteBackendError.Timeout -> {
        R.string.snackbar_http_timeout_error
    }

    DataSourceError.RemoteBackendError.NetworkError -> {
        R.string.snackbar_http_network_internet
    }

    DataSourceError.RemoteBackendError.UnknownError -> {
        R.string.snackbar_unknown_error
    }

    DataSourceError.SharedStorageError.NotFound -> {
        R.string.snackbar_shared_storage_not_found_error
    }

    DataSourceError.SharedStorageError.InvalidFile -> {
        R.string.snackbar_shared_storage_invalid_file_error
    }

    DataSourceError.SharedStorageError.UnknownError -> {
        R.string.snackbar_unknown_error
    }
}
