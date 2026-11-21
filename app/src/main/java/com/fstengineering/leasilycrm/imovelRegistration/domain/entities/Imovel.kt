package com.fstengineering.leasilycrm.imovelRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import com.fstengineering.leasilycrm.core.domain.valueObjects.Quantidade
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class Imovel private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val proprietarioId: Long,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val cepPostalCode: String?,
    val number: Int?,
    val addressLine2: String?,
    val parkingSpotNumber: String?,
    val parkingBasementLevelNumber: String?,
    val hobbyBoxNumber: String?,
    val bedroomsCount: Quantidade,
    val enSuitesCount: Quantidade,
    val doubleBedsCount: Quantidade,
    val singleBedsCount: Quantidade,
    val bathroomsCount: Quantidade,
    val parkingSpotsCount: Quantidade,
    val airConditionersCount: Quantidade,
    val maxHospedesCount: Quantidade,
    val seaProximityType: ImovelSeaProximityType?,
    val includedItems: String?,
    val hasIncludedItems: Boolean,
    val characteristics: ImmutableList<ImovelCharacteristic>,
    val cleaningServiceTax: AppDecimal?,
    val hasCleaningServiceIncluded: Boolean,
    val prices: ImmutableList<ImovelPrice>,
    val files: ImmutableList<ImovelFile>,
    val observations: String?,
) {
    companion object {
        fun parse(
            id: Long? = null,
            createDateTime: AppDateTime? = null,
            proprietarioId: Long? = null,
            type: ImovelType? = null,
            neighborhood: String? = null,
            streetAddress: String? = null,
            cepPostalCode: String? = null,
            number: Int? = null,
            addressLine2: String? = null,
            parkingSpotNumber: String? = null,
            parkingBasementLevelNumber: String? = null,
            hobbyBoxNumber: String? = null,
            bedroomsCount: Int = 0,
            enSuitesCount: Int = 0,
            doubleBedsCount: Int = 0,
            singleBedsCount: Int = 0,
            bathroomsCount: Int = 0,
            parkingSpotsCount: Int = 0,
            airConditionersCount: Int = 0,
            maxHospedesCount: Int = 0,
            seaProximityTypeId: Long? = null,
            distanceToSea: AppDecimal? = null,
            includedItems: String? = null,
            hasIncludedItems: Boolean = false,
            characteristics: ImmutableList<ImovelCharacteristic> = persistentListOf(),
            cleaningServiceTax: AppDecimal? = null,
            hasCleaningServiceIncluded: Boolean = false,
            prices: ImmutableList<ImovelPrice> = persistentListOf(),
            files: ImmutableList<ImovelFile> = persistentListOf(),
            observations: String? = null,
        ): AppResult<Imovel> {
            val errors = mutableListOf<DomainError>()

            if (proprietarioId == null) errors.add(DomainError.ImovelProprietario.Required)
            if (type == null) errors.add(DomainError.ImovelType.Required)

            if (neighborhood.isNullOrBlank()) errors.add(DomainError.Neighborhood.Required)
            if (streetAddress.isNullOrBlank()) errors.add(DomainError.ImovelStreetAddress.Required)

            val seaProximityType = seaProximityTypeId?.let { typeId ->
                ImovelSeaProximityType.parse(typeId, distanceToSea = distanceToSea)
                    .fold(onError = { errors.addAll(it.toDomainErrors()); null }, onSuccess = { it })
            }

            if (files.count { it.isMainPicture } > 1) {
                errors.add(DomainError.ImovelOtherFields.MoreThanOneMainPictureFile)
            }

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = Imovel(
                id = id,
                createDateTime = createDateTime,
                proprietarioId = requireNotNull(proprietarioId),
                type = requireNotNull(type),
                neighborhood = requireNotNull(neighborhood),
                streetAddress = requireNotNull(streetAddress),
                cepPostalCode = cepPostalCode,
                number = number,
                addressLine2 = addressLine2,
                parkingSpotNumber = parkingSpotNumber,
                parkingBasementLevelNumber = parkingBasementLevelNumber,
                hobbyBoxNumber = hobbyBoxNumber,
                bedroomsCount = Quantidade.of(bedroomsCount),
                enSuitesCount = Quantidade.of(enSuitesCount),
                doubleBedsCount = Quantidade.of(doubleBedsCount),
                singleBedsCount = Quantidade.of(singleBedsCount),
                bathroomsCount = Quantidade.of(bathroomsCount),
                parkingSpotsCount = Quantidade.of(parkingSpotsCount),
                airConditionersCount = Quantidade.of(airConditionersCount),
                maxHospedesCount = Quantidade.of(maxHospedesCount),
                seaProximityType = seaProximityType,
                includedItems = includedItems,
                hasIncludedItems = hasIncludedItems,
                characteristics = characteristics,
                cleaningServiceTax = cleaningServiceTax,
                hasCleaningServiceIncluded = hasCleaningServiceIncluded,
                prices = prices,
                files = files,
                observations = observations,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
