package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface UndoRegisterStayCheckOutUseCase {
    suspend operator fun invoke(stay: Stay.Owned): AppResult<Unit, DataError>
}
