package com.fstengineering.leasilycrm.core.di.qualifiers

import org.koin.core.qualifier.named

// Assistance registration
val readAssistanceQualifier = named("readAssistance")
val writeAssistanceQualifier = named("writeAssistance")

// Lead/tenant registration
val writeLeadTenantQualifier = named("writeLeadTenant")

// Property owner registration
val writePropertyOwnerQualifier = named("writePropertyOwner")

// Profile
val writeUserQualifier = named("writeUser")

// Stay details
val watchOccupiedPeriodQualifier = named("watchOccupiedPeriod")

// Core use cases
val registerStayCheckOutQualifier = named("registerStayCheckOut")
val undoRegisterStayCheckOutQualifier = named("undoRegisterStayCheckOut")

// Property dashboard
val watchPropertyQualifier = named("watchProperty")

// Property occupation
val watchPropertyOccupationQualifier = named("watchPropertyOccupation")

// Stay registration
val readStayQualifier = named("readStay")
val readUnavailablePeriodQualifier = named("readUnavailablePeriod")
val readSelectablePropertiesQualifier = named("readSelectableProperties")
val writeStayQualifier = named("writeStay")
val writeUnavailablePeriodQualifier = named("writeUnavailablePeriod")

// Lead tenant details
val watchLeadTenantQualifier = named("watchLeadTenant")
val deleteLeadTenantQualifier = named("deleteLeadTenant")
val deleteAssistanceQualifier = named("deleteAssistance")

// Property registration
val readPropertyQualifier = named("readProperty")
val writePropertyQualifier = named("writeProperty")
