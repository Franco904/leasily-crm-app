package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CrmMetricsDao
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
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CrmMetricsDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class CrmMetricsDatabaseDataSourceImpl(
    private val dao: CrmMetricsDao,
) : CrmMetricsDatabaseDataSource {
    override fun watchGeneratedLeadsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchGeneratedLeadsPerMonth(userId) }

    override fun watchGeneratedLeadsPerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerTenantSegmentPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchGeneratedLeadsPerTenantSegmentPerMonth(userId) }

    override fun watchCountLeadsByContactChannelType(
        userId: Long,
    ): AppResult<Flow<List<LeadContactChannelCountCompound>>, DataError> =
        onDatabaseSafe { dao.watchCountLeadsByContactChannelType(userId) }

    override fun watchNewStayBookingsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerMonth(userId) }

    override fun watchNewStayBookingsPerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerCustomerSegmentPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTenantSegmentPerMonth(userId) }

    override fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerCustomerSegmentPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerPropertyOwnerSegmentPerMonth(userId) }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTop3NeighborhoodsPerMonth(userId) }

    override fun watchCommissionInvoicePerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerMonth(userId) }

    override fun watchCommissionInvoicePerTenantSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerCustomerSegmentPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTenantSegmentPerMonth(userId) }

    override fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerCustomerSegmentPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerPropertyOwnerSegmentPerMonth(userId) }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTop3NeighborhoodsPerMonth(userId) }

    override fun watchAverageTicketPerMonth(
        userId: Long,
    ): AppResult<Flow<List<AverageTicketPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchAverageTicketPerMonth(userId) }
}
