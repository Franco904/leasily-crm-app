package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayValidator
import java.math.BigDecimal
import java.time.LocalDateTime

class StayValidatorImpl : StayValidator {
    override fun validate(
        stay: Stay,
        stayBeforeEdit: Stay?,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateProperty(property = stay.property)
            .onError { e -> errors.add(e) }

        validatePeriod(
            checkInDateTime = stay.checkInDateTime?.withHour(0)?.withMinute(0),
            checkOutDateTime = stay.checkOutDateTime?.withHour(0)?.withMinute(0),
            checkInDateTimeBeforeEdit = stayBeforeEdit?.checkInDateTime?.withHour(0)?.withMinute(0),
            propertyStayCheckInDateTimes = stay.property?.stays
                ?.mapNotNull { it.checkInDateTime?.withHour(0)?.withMinute(0) }
                ?: emptyList(),
        )
            .onError { e -> errors.add(e) }

        if (!stay.isUnavailablePeriod) {
            validateTenant(tenant = stay.tenant)
                .onError { e -> errors.add(e) }

            validateCheckInTime(checkInHourMinute = stay.checkInHourMinute)
                .onError { e -> errors.add(e) }

            validateCheckOutTime(checkOutHourMinute = stay.checkOutHourMinute)
                .onError { e -> errors.add(e) }

            validateCommission(commission = stay.commission)
                .onError { e -> errors.add(e) }
        }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateProperty(
        property: Property?,
    ): AppResult<Unit, DomainError.StayProperty> {
        return when {
            property == null -> {
                AppResult.Error(DomainError.StayProperty.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateTenant(
        tenant: Tenant?,
    ): AppResult<Unit, DomainError.StayTenant> {
        return when {
            tenant == null -> {
                AppResult.Error(DomainError.StayTenant.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePeriod(
        checkInDateTime: LocalDateTime?,
        checkOutDateTime: LocalDateTime?,
        checkInDateTimeBeforeEdit: LocalDateTime?,
        propertyStayCheckInDateTimes: List<LocalDateTime>,
    ): AppResult<Unit, DomainError.StayPeriod> {
        when {
            checkInDateTime == null || checkOutDateTime == null -> {
                return AppResult.Error(DomainError.StayPeriod.IsNull)
            }

            checkInDateTime.toLocalDate() == checkOutDateTime.toLocalDate() -> {
                return AppResult.Error(DomainError.StayPeriod.StartAndEndDaysAreTheSame)
            }

            propertyStayCheckInDateTimes.isNotEmpty() -> {
                val isThereAnyPropertyOverbooking = propertyStayCheckInDateTimes
                    .filter { it != checkInDateTimeBeforeEdit }
                    .any { existingStayCheckInDateTime ->
                        checkInDateTime <= existingStayCheckInDateTime
                                && checkOutDateTime >= existingStayCheckInDateTime
                    }

                if (!isThereAnyPropertyOverbooking) return AppResult.Success(Unit)

                return AppResult.Error(DomainError.StayPeriod.IsOverlappingOtherPropertyStayPeriod)
            }

            else -> return AppResult.Success(Unit)
        }
    }

    private fun validateCheckInTime(
        checkInHourMinute: Pair<Int?, Int?>,
    ): AppResult<Unit, DomainError.StayCheckInTime> {
        return when {
            checkInHourMinute.toList().any { it == null } -> {
                AppResult.Error(DomainError.StayCheckInTime.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateCheckOutTime(
        checkOutHourMinute: Pair<Int?, Int?>,
    ): AppResult<Unit, DomainError.StayCheckOutTime> {
        return when {
            checkOutHourMinute.toList().any { it == null } -> {
                AppResult.Error(DomainError.StayCheckOutTime.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateCommission(
        commission: BigDecimal?,
    ): AppResult<Unit, DomainError.StayCommission> {
        return when {
            commission == null -> {
                AppResult.Error(DomainError.StayCommission.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
