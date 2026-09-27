package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
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
import kotlinx.coroutines.flow.Flow

interface UserAnalyticsDatabaseDataSource {
    fun watchGeneratedLeadsPerMonth(): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, ApplicationError>

    fun watchGeneratedLeadsPerTenantTagPerMonth(): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, ApplicationError>

    fun watchCountLeadsByContactChannelType(): AppResult<Flow<List<LeadContactChannelCountCompound>>, ApplicationError>

    fun watchNewStayBookingsPerMonth(): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, ApplicationError>

    fun watchNewStayBookingsPerTenantTagPerMonth(): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, ApplicationError>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, ApplicationError>

    fun watchCommissionInvoicePerMonth(): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, ApplicationError>

    fun watchCommissionInvoicePerTenantTagPerMonth(): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, ApplicationError>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, ApplicationError>

    fun watchAverageTicketPerMonth(): AppResult<Flow<List<AverageTicketPerMonthCompound>>, ApplicationError>
}
