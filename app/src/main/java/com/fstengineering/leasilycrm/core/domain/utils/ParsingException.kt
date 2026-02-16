package com.fstengineering.leasilycrm.core.domain.utils

class ParsingException(val error: ParsingError) : IllegalStateException(error.toString())
