package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

fun PropertyType.presentationLabel() = when (this) {
    PropertyType.Apartment -> R.string.apartment_chip_text
    PropertyType.House -> R.string.house_chip_text
}
