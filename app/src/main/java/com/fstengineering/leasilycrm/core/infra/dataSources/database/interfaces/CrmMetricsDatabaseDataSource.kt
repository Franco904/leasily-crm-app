package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AverageTicketPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.CommissionInvoicePerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.CommissionInvoicePerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.CommissionInvoicePerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.GeneratedLeadsPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.GeneratedLeadsPerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.LeadContactChannelCountCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.NewStayBookingsPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.NewStayBookingsPerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.NewStayBookingsPerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface CrmMetricsDatabaseDataSource {
    fun watchGeneratedLeadsPerMonth(): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, InfraError>

    fun watchGeneratedLeadsPerTenantTagPerMonth(): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, InfraError>

    fun watchCountLeadsByContactChannelType(): AppResult<Flow<List<LeadContactChannelCountCompound>>, InfraError>

    fun watchNewStayBookingsPerMonth(): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, InfraError>

    fun watchNewStayBookingsPerTenantTagPerMonth(): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, InfraError>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, InfraError>

    fun watchCommissionInvoicePerMonth(): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, InfraError>

    fun watchCommissionInvoicePerTenantTagPerMonth(): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, InfraError>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, InfraError>

    fun watchAverageTicketPerMonth(): AppResult<Flow<List<AverageTicketPerMonthCompound>>, InfraError>
}
