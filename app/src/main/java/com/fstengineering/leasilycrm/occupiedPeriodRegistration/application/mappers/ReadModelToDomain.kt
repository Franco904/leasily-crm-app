package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.mappers

import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects.OccupiedPeriodInterval
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

fun ImmutableList<OccupiedPeriodReadModel>.toIntervals(): ImmutableList<OccupiedPeriodInterval> =
    map {
        OccupiedPeriodInterval(
            stayId = it.stayId,
            checkInDateTime = it.checkInDateTime,
            checkOutDateTime = it.checkOutDateTime,
        )
    }.toPersistentList()
