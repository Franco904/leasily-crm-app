package com.fstengineering.leasilycrm.profile.models

import androidx.annotation.StringRes

data class EditUserFormUiState(
    @StringRes val nameError: Int? = null,
    @StringRes val creciNumberError: Int? = null,
)
