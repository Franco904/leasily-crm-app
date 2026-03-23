package com.fstengineering.leasilycrm.signin.application.stores

import com.fstengineering.leasilycrm.signin.application.models.SignInResultWriteModel

interface UserSessionStore {
    suspend fun storeSignedInUser(signInResult: SignInResultWriteModel)
}
