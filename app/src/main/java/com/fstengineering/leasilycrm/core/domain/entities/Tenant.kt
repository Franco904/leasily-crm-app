package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validations.validateEmail
import com.fstengineering.leasilycrm.core.domain.validations.validateName
import com.fstengineering.leasilycrm.core.domain.validations.validatePhone
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import kotlinx.collections.immutable.ImmutableList

class Tenant private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val tag: String?,
    val name: String,
    val city: String?,
    val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val contactOrigin: String?,
    val observations: String?,
    val status: TenantStatus,
    val isDeleted: Boolean,
    val assistances: ImmutableList<Assistance>,
    val stays: ImmutableList<Stay>,
    val inconsistencies: String?,
    val contractVersion: Int,
) {
    val isLead get() = status == TenantStatus.Lead
    val isOld get() = status == TenantStatus.Old

    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            updateDateTime: AppDateTime?,
            tag: String?,
            name: String?,
            city: String?,
            identityDocument: String?,
            phone: String?,
            email: String?,
            contactOrigin: String?,
            observations: String?,
            statusId: Int,
            isDeleted: Boolean,
            assistances: ImmutableList<Assistance>,
            stays: ImmutableList<Stay>,
            inconsistencies: String?,
            contractVersion: Int,
        ): Tenant {
            validateName(name).onError { throw it.asException() }
            phone?.let { validatePhone(phone).onError { throw it.asException() } }
            email?.let { validateEmail(email).onError { throw it.asException() } }

            val status = TenantStatus.parse(statusId)

            return Tenant(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                tag = tag,
                name = requireNotNull(name),
                city = city,
                identityDocument = identityDocument,
                phone = phone,
                email = email,
                contactOrigin = contactOrigin,
                observations = observations,
                status = status,
                isDeleted = isDeleted,
                assistances = assistances,
                stays = stays,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }
    }
}

//     val isAnyFormFieldInitialized
//        get() = tag != null
//                || name != null
//                || city != null
//                || identityDocument != null
//                || phone != null
//                || email != null
//                || contactOrigin != null
//                || observations != null
