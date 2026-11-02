package com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableList

data class ImovelReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val owner: ProprietarioReadModel,
    val currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
) {
    companion object {
        fun create(
            imovelRaw: ImovelRawReadModel,
            owner: ProprietarioReadModel,
            currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
        ) = ImovelReadModel(
            id = imovelRaw.id,
            mainPictureUriString = imovelRaw.mainPictureUriString,
            type = imovelRaw.type,
            neighborhood = imovelRaw.neighborhood,
            streetAddress = imovelRaw.streetAddress,
            number = imovelRaw.number,
            owner = owner,
            currentMonthOccupiedPeriods = currentMonthOccupiedPeriods,
        )
    }
}
