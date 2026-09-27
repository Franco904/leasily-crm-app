package com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
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
        ): AppResult<PropertyOwner, ValidationErrors> {
            val errors = mutableListOf<DomainError>()

            if (name == null) errors.add(DomainError.Name.Required)

            val parsedName = name?.let { Name.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedPhone = phone?.let { Phone.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            val parsedEmail = email?.let { Email.parse(it) }
                ?.fold(onError = { errors.addAll(it.errors); null }, onSuccess = { it })

            val parsedPaymentKey = paymentKey?.let { PaymentKey.parse(it) }
                ?.fold(onError = { errors.add(it); null }, onSuccess = { it })

            if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

            val parsed = PropertyOwner(
                id = id,
                createDateTime = createDateTime,
                name = requireNotNull(parsedName),
                phone = parsedPhone,
                email = parsedEmail,
                paymentKey = parsedPaymentKey,
                files = files,
            )

            return AppResult.Success(data = parsed)
        }
    }
}
