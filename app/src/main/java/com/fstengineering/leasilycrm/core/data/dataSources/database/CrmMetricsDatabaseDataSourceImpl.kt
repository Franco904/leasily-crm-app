package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CrmMetricsDao
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

    override fun watchGeneratedLeadsPerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchGeneratedLeadsPerTenantTagPerMonth(userId) }

    override fun watchCountLeadsByContactChannelType(
        userId: Long,
    ): AppResult<Flow<List<LeadContactChannelCountCompound>>, DataError> =
        onDatabaseSafe { dao.watchCountLeadsByContactChannelType(userId) }

    override fun watchNewStayBookingsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerMonth(userId) }

    override fun watchNewStayBookingsPerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTenantTagPerMonth(userId) }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTop3NeighborhoodsPerMonth(userId) }

    override fun watchCommissionInvoicePerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerMonth(userId) }

    override fun watchCommissionInvoicePerTenantTagPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTenantTagPerMonth(userId) }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(
        userId: Long,
    ): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTop3NeighborhoodsPerMonth(userId) }

    override fun watchAverageTicketPerMonth(
        userId: Long,
    ): AppResult<Flow<List<AverageTicketPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchAverageTicketPerMonth(userId) }
}
