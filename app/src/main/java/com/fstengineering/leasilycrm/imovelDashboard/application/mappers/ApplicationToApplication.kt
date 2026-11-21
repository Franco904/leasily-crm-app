package com.fstengineering.leasilycrm.imovelDashboard.application.mappers

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelSummaryReadModel
import kotlinx.collections.immutable.ImmutableList

fun ImovelSummaryReadModel.toReadModel(
    currentMonthOccupiedPeriods: Pair<AppYearMonth, ImmutableList<ImovelOccupiedDateReadModel>>,
) = ImovelReadModel(
    id = id,
    mainPictureUriString = mainPictureUriString,
    type = type,
    neighborhood = neighborhood,
    streetAddress = streetAddress,
    number = number,
    proprietario = proprietario,
    currentMonthOccupiedPeriods = currentMonthOccupiedPeriods,
)
