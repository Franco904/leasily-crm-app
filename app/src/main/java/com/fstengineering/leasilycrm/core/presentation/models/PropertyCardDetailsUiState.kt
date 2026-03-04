package com.fstengineering.leasilycrm.core.presentation.models

import android.net.Uri
import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class PropertyCardDetailsUiState(
    val id: Long,
    val mainPictureUriString: Uri?,
    val neighborhood: String,
    val streetAddressAndNumber: String,
    val type: PropertyType,
    val seaProximityTypeId: Long?,
    val distanceToSea: String?,
    val bedroomsCount: Int,
) {
    companion object {
        fun fromDomain(property: Property): PropertyCardDetailsUiState {
            val numberText = if (property.number != null) ", ${property.number}" else ""

            return PropertyCardDetailsUiState(
                id = requireNotNull(property.id),
                mainPictureUriString = property.files
                    .find { picture -> picture.isMainPicture }?.uriString?.toUri(),
                neighborhood = property.neighborhood,
                streetAddressAndNumber = "${property.streetAddress}$numberText",
                type = property.type,
                seaProximityTypeId = property.seaProximityType?.id,
                distanceToSea = (property.seaProximityType as? PropertySeaProximityType.DistanceToSea)
                    ?.distance?.toString(),
                bedroomsCount = property.bedrooms,
            )
        }
    }
}
