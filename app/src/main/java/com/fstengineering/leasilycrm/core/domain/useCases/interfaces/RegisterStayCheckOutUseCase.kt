package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface RegisterStayCheckOutUseCase {
    suspend operator fun invoke(
        stay: Stay,
    ): AppResult<Unit, DataSourceError>
}
