package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateEmail
import com.fstengineering.leasilycrm.core.domain.validations.validateName
import com.fstengineering.leasilycrm.core.domain.validations.validatePhone
import kotlinx.collections.immutable.ImmutableList

class PropertyOwner private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: String,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFile>,
    val isDeleted: Boolean,
    val inconsistencies: String?,
    val contractVersion: Int,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            name: String?,
            phone: String?,
            email: String?,
            paymentKey: String?,
            files: ImmutableList<PropertyOwnerFile>,
            isDeleted: Boolean,
            inconsistencies: String?,
            contractVersion: Int,
        ): PropertyOwner {
            validateName(name).onError { throw it.asException() }
            phone?.let { validatePhone(phone).onError { throw it.asException() } }
            email?.let { validateEmail(email).onError { throw it.asException() } }

            return PropertyOwner(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                name = requireNotNull(name),
                phone = phone,
                email = email,
                paymentKey = paymentKey,
                isDeleted = isDeleted,
                files = files,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }
    }
}

// val isAnyFormFieldInitialized = name != null
//            || phone != null
//            || email != null
//            || paymentKey != null
//            || files.isNotEmpty()
