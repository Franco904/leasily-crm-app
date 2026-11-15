package com.fstengineering.leasilycrm.imovelDashboard.presentation.mappers

import androidx.core.net.toUri
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelOccupiedDateReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelAddressUiState
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelOccupiedDateUiState
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelUiState
import kotlinx.collections.immutable.toPersistentList

fun ImovelReadModel.toUiState(): ImovelUiState.Loaded {
    val (currentYearMonth, occupiedPeriods) = currentMonthOccupiedPeriods

    return ImovelUiState.Loaded(
        id = id,
        mainPicture = mainPictureUriString?.let {
            ImovelFileUiState(uri = it.toUri(), isMainPicture = true)
        },
        type = type,
        address = ImovelAddressUiState(
            neighborhood = neighborhood,
            streetAddress = streetAddress,
            number = number,
        ),
        proprietario = proprietario.toUiState(),
        currentMonthOccupiedPeriods = currentYearMonth to occupiedPeriods
            .map { it.toUiState() }
            .toPersistentList(),
    )
}

private fun ImovelOccupiedDateReadModel.toUiState() = ImovelOccupiedDateUiState(
    dayOfMonth = date.dayOfMonth.toString(),
    firstHalfPeriodIndex = firstHalfPeriodIndex,
    secondHalfPeriodIndex = secondHalfPeriodIndex,
    periodId = periodId,
    tenantName = tenantName,
)

private fun ProprietarioReadModel.toUiState() = ProprietarioUiState(
    id = id,
    name = name,
)
