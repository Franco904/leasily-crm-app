package com.fstengineering.leasilycrm.core.infra.di.qualifiers

import org.koin.core.qualifier.named

// Assistance registration
val readAssistanceQualifier = named("readAssistance")
val writeAssistanceQualifier = named("writeAssistance")

// Profile
val writeUserQualifier = named("writeUser")
