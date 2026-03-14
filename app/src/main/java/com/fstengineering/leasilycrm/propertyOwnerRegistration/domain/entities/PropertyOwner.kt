package com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.Email
import com.fstengineering.leasilycrm.core.domain.valueObjects.Name
import com.fstengineering.leasilycrm.core.domain.valueObjects.PaymentKey
import com.fstengineering.leasilycrm.core.domain.valueObjects.Phone
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class PropertyOwner private constructor(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val name: Name,
    val phone: Phone?,
    val email: Email?,
    val paymentKey: PaymentKey?,
    val files: ImmutableList<PropertyOwnerFile>,
) {
    companion object {
        fun parse(
            id: Long?,
            createDateTime: AppDateTime?,
            name: String?,
            phone: String?,
            email: String?,
            paymentKey: String?,
            files: ImmutableList<PropertyOwnerFile>,
        ): AppResultCompound<PropertyOwner, DomainError> {
            val errors = mutableListOf<DomainError>()

            if (name == null) errors.add(DomainError.Name.Required)

            val parsedName = name?.let { Name.parse(name) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedPhone = phone?.let { Phone.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedEmail = email?.let { Email.parse(it) }
                ?.fold(onErrors = { errors.addAll(it); null }, onSuccess = { it })

            val parsedPaymentKey = paymentKey?.let { PaymentKey.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = PropertyOwner(
                id = id,
                createDateTime = createDateTime,
                name = requireNotNull(parsedName),
                phone = parsedPhone,
                email = parsedEmail,
                paymentKey = parsedPaymentKey,
                files = files,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
