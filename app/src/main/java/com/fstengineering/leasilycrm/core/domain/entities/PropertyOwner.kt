package com.fstengineering.leasilycrm.core.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Email
import com.fstengineering.leasilycrm.core.domain.valueObjects.Name
import com.fstengineering.leasilycrm.core.domain.valueObjects.Phone
import kotlinx.collections.immutable.ImmutableList

class PropertyOwner private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: Name,
    val phone: Phone?,
    val email: Email?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFile>,
    val inconsistencies: ImmutableList<DomainError>,
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
            inconsistencies: ImmutableList<DomainError>,
            contractVersion: Int,
        ): PropertyOwner {
            val parsedName = Name.parse(name)
            val parsedPhone = phone?.let { Phone.parse(it) }
            val parsedEmail = email?.let { Email.parse(it) }

            return PropertyOwner(
                id = id,
                createDateTime = createDateTime,
                updateDateTime = updateDateTime,
                name = parsedName,
                phone = parsedPhone,
                email = parsedEmail,
                paymentKey = paymentKey,
                files = files,
                inconsistencies = inconsistencies,
                contractVersion = contractVersion,
            )
        }
    }
}
