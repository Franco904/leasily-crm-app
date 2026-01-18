package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.AverageTicketPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerCustomerSegmentPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CommissionInvoicePerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerTenantSegmentPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.LeadContactChannelCountCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerCustomerSegmentPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.NewStayBookingsPerTop3NeighborhoodsPerMonthCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface CrmMetricsDatabaseDataSource {
    fun watchGeneratedLeadsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, DataError>

    fun watchGeneratedLeadsPerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerTenantSegmentPerMonthCompound>>, DataError>

    fun watchCountLeadsByContactChannelType(
        userId: Long,
    ): AppResult<Flow<List<LeadContactChannelCountCompound>>, DataError>

    fun watchNewStayBookingsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, DataError>

    fun watchNewStayBookingsPerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerCustomerSegmentPerMonthCompound>>, DataError>

    fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerCustomerSegmentPerMonthCompound>>, DataError>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerCustomerSegmentPerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerCustomerSegmentPerMonthCompound>>, DataError>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, DataError>

    fun watchAverageTicketPerMonth(
        userId: Long,
    ): AppResult<Flow<List<AverageTicketPerMonthCompound>>, DataError>
}
