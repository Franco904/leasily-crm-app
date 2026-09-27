package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.mappers

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerFileCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwnerFile
import kotlinx.collections.immutable.toPersistentList

fun PropertyOwnerCommandModel.toEntity(): AppResult<PropertyOwner, ValidationErrors> {
    val errors = mutableListOf<DomainError>()

    val parsed = PropertyOwner.parse(
        id = id,
        createDateTime = createDateTime,
        name = name,
        phone = phone,
        email = email,
        paymentKey = paymentKey,
        files = files.map { it.toEntity() }.toPersistentList(),
    )
        .fold(onError = { errors.addAll(0, it.errors); null }, onSuccess = { it })

    if (errors.isNotEmpty()) return AppResult.Error(ValidationErrors(errors.toPersistentList()))

    return AppResult.Success(data = requireNotNull(parsed))
}

private fun PropertyOwnerFileCommandModel.toEntity() = PropertyOwnerFile(
    id = id,
    uriString = uriString,
    name = name,
)
