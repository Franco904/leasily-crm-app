package com.fstengineering.leasilycrm.profile.models

import androidx.annotation.StringRes

data class EditUserErrorUiState(
    @StringRes val nameError: Int? = null,
    @StringRes val creciNumberError: Int? = null,
)
