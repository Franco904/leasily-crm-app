package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface UserValidator {
    fun validate(user: User): AppResultCompound<Unit, ValidationError>
}
