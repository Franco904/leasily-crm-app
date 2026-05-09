package com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.Email
import com.fstengineering.leasilycrm.core.domain.valueObjects.Name
import com.fstengineering.leasilycrm.core.domain.valueObjects.Phone
import kotlinx.collections.immutable.toPersistentList

class LeadTenant private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val tag: String?,
    val name: Name,
    val city: String?,
    val identityDocument: String?,
    val phone: Phone?,
    val email: Email?,
    val contactOrigin: String?,
    val observations: String?,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            tag: String?,
            name: String?,
            city: String?,
            identityDocument: String?,
            phone: String?,
            email: String?,
            contactOrigin: String?,
            observations: String?,
        ): AppResultCompound<LeadTenant, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (name == null) errors.add(DomainError.Name.Required)

            val parsedName = name?.let { Name.parse(name) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedPhone = phone?.let { Phone.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedEmail = email?.let { Email.parse(it) }
                ?.fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = LeadTenant(
                id = id,
                createDateTime = createDateTime,
                tag = tag,
                name = requireNotNull(parsedName),
                city = city,
                identityDocument = identityDocument,
                phone = parsedPhone,
                email = parsedEmail,
                contactOrigin = contactOrigin,
                observations = observations,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
