package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface AssistanceSearchedNeighborhoodValidator {
    fun validate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResultCompound<Unit, ValidationError>
}
