package com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers

import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.ProprietarioFile

fun Proprietario.toProprietarioOrm(): ProprietarioOrm {
    return ProprietarioOrm(
        id = id,
        createDateTime = createDateTime ?: AppDateTime.now(),
        updateDateTime = AppDateTime.now(),
        name = name.value.normalizeEncoding(),
        phone = phone?.value,
        email = email?.value,
        paymentKey = paymentKey?.value,
        isDeleted = false,
        contractVersion = BuildConfig.CONTRACT_VERSION,
    )
}

fun ProprietarioFile.toProprietarioFileOrm(
    proprietarioId: Long,
): ProprietarioFileOrm {
    return ProprietarioFileOrm(
        id = id,
        proprietarioFileUri = uriString,
        proprietarioFileName = name,
        proprietarioId = proprietarioId,
    )
}
