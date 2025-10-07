package com.fstengineering.leasilycrm.core.application.models

import java.time.LocalDate

data class LazyAppDate(val date: () -> LocalDate)
