package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models

import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

data class PropertyOwnerCommandModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val updateDateTime: AppDateTime?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val paymentKey: String?,
    val files: ImmutableList<PropertyOwnerFileCommandModel>,
) : CommandUseCase.CommandModel {
    fun toAggregate(): AppResultCompound<PropertyOwner, DomainError> {
        val errors = mutableListOf<DomainError>()

        val parsed = PropertyOwner.parse(
            id = id,
            createDateTime = createDateTime,
            updateDateTime = updateDateTime,
            name = name,
            phone = phone,
            email = email,
            paymentKey = paymentKey,
            files = files.map { it.toEntity() }.toPersistentList(),
        )
            .fold(onErrors = { errors.addAll(0, it); null }, onSuccess = { it })

        if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

        return AppResultCompound.Success(data = requireNotNull(parsed))
    }
}
