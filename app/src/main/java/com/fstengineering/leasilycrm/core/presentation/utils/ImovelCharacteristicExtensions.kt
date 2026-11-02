package com.fstengineering.leasilycrm.core.presentation.utils

import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic

fun ImovelCharacteristic.presentationLabel() = when (this) {
    ImovelCharacteristic.Accessibility -> R.string.accessibility_property_characteristic_text
    ImovelCharacteristic.Balcony -> R.string.balcony_property_characteristic_text
    ImovelCharacteristic.BarbecueArea -> R.string.barbecue_area_property_characteristic_text
    ImovelCharacteristic.BathLinen -> R.string.bath_linen_property_characteristic_text
    ImovelCharacteristic.BeachChair -> R.string.beach_chair_property_characteristic_text
    ImovelCharacteristic.BeachUmbrella -> R.string.beach_umbrella_property_characteristic_text
    ImovelCharacteristic.BedLinen -> R.string.bed_linen_property_characteristic_text
    ImovelCharacteristic.CommercialArea -> R.string.commercial_area_property_characteristic_text
    ImovelCharacteristic.CommonAreaCondominium -> R.string.common_area_condominium_property_characteristic_text
    ImovelCharacteristic.Elevator -> R.string.elevator_property_characteristic_text
    ImovelCharacteristic.HomeOffice -> R.string.home_office_property_characteristic_text
    ImovelCharacteristic.Modern -> R.string.modern_property_characteristic_text
    ImovelCharacteristic.PetFriendly -> R.string.pet_friendly_property_characteristic_text
    ImovelCharacteristic.Playground -> R.string.playground_property_characteristic_text
    ImovelCharacteristic.SwimmingPool -> R.string.swimming_pool_property_characteristic_text
    ImovelCharacteristic.WashingMachine -> R.string.washing_machine_property_characteristic_text
}
