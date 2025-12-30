package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface AssistanceSearchedNeighborhoodValidator {
    fun validate(
        searchedNeighborhood: AssistanceSearchedNeighborhood,
    ): AppResultCompound<Unit, DomainError>
}
