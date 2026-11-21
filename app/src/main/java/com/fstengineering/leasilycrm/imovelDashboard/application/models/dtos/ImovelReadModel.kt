package com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType
import kotlinx.collections.immutable.ImmutableList

/** [proprietario] é `null` quando o Proprietário do Imóvel foi excluído. */
data class ImovelReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val proprietario: ProprietarioReadModel?,
    val currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
)
