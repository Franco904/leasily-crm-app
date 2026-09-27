package com.fstengineering.leasilycrm.profile.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.domain.entities.User

fun UserCommandModel.toEntity(): AppResult<User, ValidationErrors> {
    return User.parse(creciNumber = creciNumber)
}
