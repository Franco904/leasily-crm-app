package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Email
import com.fstengineering.leasilycrm.core.domain.valueObjects.Name
import com.fstengineering.leasilycrm.core.domain.valueObjects.Phone
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import kotlinx.collections.immutable.ImmutableList

class Tenant private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val tag: String?,
    val name: Name,
    val city: String?,
    val identityDocument: String?,
    val phone: Phone?,
    val email: Email?,
    val contactOrigin: String?,
    val observations: String?,
    val status: TenantStatus,
    val assistancesIds: ImmutableList<Long>,
    val staysIds: ImmutableList<Long>,
    val inconsistencies: ImmutableList<DomainError>,
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
            assistancesIds: ImmutableList<Long>,
            staysIds: ImmutableList<Long>,
            inconsistencies: ImmutableList<DomainError>,
            contractVersion: Int,
        ): Tenant {
            val parsedName = Name.parse(name)
            val parsedPhone = phone?.let { Phone.parse(it) }
            val parsedEmail = email?.let { Email.parse(it) }

            val status = TenantStatus.parse(statusId)

            return Tenant(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                tag = tag,
                name = parsedName,
                city = city,
                identityDocument = identityDocument,
                phone = parsedPhone,
                email = parsedEmail,
                contactOrigin = contactOrigin,
                observations = observations,
                status = status,
                assistancesIds = assistancesIds,
                staysIds = staysIds,
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
