package com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelType

/** Imóvel do painel com o Proprietário, sem os Períodos Ocupados do mês, que o UseCase compõe. */
data class ImovelSummaryReadModel(
    val id: Long,
    val mainPictureUriString: String?,
    val type: ImovelType,
    val neighborhood: String,
    val streetAddress: String,
    val number: Int?,
    val proprietario: ProprietarioReadModel,
)
