package com.fstengineering.leasilycrm.core.domain.utils

class DomainException(val error: DomainError) : IllegalStateException(error.toString())
