package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.AverageTicketPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.LeadContactChannelCountCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerTenantTagPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface CrmMetricsDatabaseDataSource {
    fun watchGeneratedLeadsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, DataError>

    fun watchGeneratedLeadsPerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, DataError>

    fun watchCountLeadsByContactChannelType(
        userId: Long,
    ): AppResult<Flow<List<LeadContactChannelCountCompound>>, DataError>

    fun watchNewStayBookingsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, DataError>

    fun watchNewStayBookingsPerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, DataError>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, DataError>

    fun watchAverageTicketPerMonth(
        userId: Long,
    ): AppResult<Flow<List<AverageTicketPerMonthCompound>>, DataError>
}
