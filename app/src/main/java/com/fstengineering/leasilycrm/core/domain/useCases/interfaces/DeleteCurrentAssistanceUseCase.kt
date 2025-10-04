package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface DeleteCurrentAssistanceUseCase {
    suspend operator fun invoke(
        currentAssistance: Assistance,
        hasFinishedStays: Boolean,
    ): AppResult<Unit, DataSourceError>
}
