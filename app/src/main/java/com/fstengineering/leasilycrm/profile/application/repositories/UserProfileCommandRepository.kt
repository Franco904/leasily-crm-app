package com.fstengineering.leasilycrm.profile.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.profile.domain.entities.User

interface UserProfileCommandRepository {
    suspend fun update(user: User): AppResult<Unit, ApplicationError>
}
