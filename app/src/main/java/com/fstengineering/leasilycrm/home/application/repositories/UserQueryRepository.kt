package com.fstengineering.leasilycrm.home.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface UserQueryRepository {
    fun watchUserProfilePictureUrl(): Flow<AppResult<String?, ApplicationError>>
}
