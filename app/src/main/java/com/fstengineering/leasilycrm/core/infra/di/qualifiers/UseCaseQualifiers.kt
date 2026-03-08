package com.fstengineering.leasilycrm.core.infra.di.qualifiers

import org.koin.core.qualifier.named

// Assistance registration
val readAssistanceQualifier = named("readAssistance")
val writeAssistanceQualifier = named("writeAssistance")

// Property owner registration
val writePropertyOwnerQualifier = named("writePropertyOwner")

// Profile
val writeUserQualifier = named("writeUser")
