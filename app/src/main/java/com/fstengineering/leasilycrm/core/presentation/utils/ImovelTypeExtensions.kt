package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

fun ImovelType.presentationLabel() = when (this) {
    ImovelType.Apartment -> R.string.apartment_property_type_text
    ImovelType.House -> R.string.house_property_type_text
}
