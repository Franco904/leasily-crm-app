package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyCharacteristic

fun PropertyCharacteristic.presentationLabel() = when (this) {
    PropertyCharacteristic.Accessibility -> R.string.accessibility_property_characteristic_text
    PropertyCharacteristic.Balcony -> R.string.balcony_property_characteristic_text
    PropertyCharacteristic.BarbecueArea -> R.string.barbecue_area_property_characteristic_text
    PropertyCharacteristic.BathLinen -> R.string.bath_linen_property_characteristic_text
    PropertyCharacteristic.BeachChair -> R.string.beach_chair_property_characteristic_text
    PropertyCharacteristic.BeachUmbrella -> R.string.beach_umbrella_property_characteristic_text
    PropertyCharacteristic.BedLinen -> R.string.bed_linen_property_characteristic_text
    PropertyCharacteristic.CommercialArea -> R.string.commercial_area_property_characteristic_text
    PropertyCharacteristic.CommonAreaCondominium -> R.string.common_area_condominium_property_characteristic_text
    PropertyCharacteristic.Elevator -> R.string.elevator_property_characteristic_text
    PropertyCharacteristic.HomeOffice -> R.string.home_office_property_characteristic_text
    PropertyCharacteristic.Modern -> R.string.modern_property_characteristic_text
    PropertyCharacteristic.PetFriendly -> R.string.pet_friendly_property_characteristic_text
    PropertyCharacteristic.Playground -> R.string.playground_property_characteristic_text
    PropertyCharacteristic.SwimmingPool -> R.string.swimming_pool_property_characteristic_text
    PropertyCharacteristic.WashingMachine -> R.string.washing_machine_property_characteristic_text
}
