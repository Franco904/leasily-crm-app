package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.CrmMetricsDao
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
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.CrmMetricsDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class CrmMetricsDatabaseDataSourceImpl(
    private val dao: CrmMetricsDao,
) : CrmMetricsDatabaseDataSource {
    override fun watchGeneratedLeadsPerMonth(): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchGeneratedLeadsPerMonth() }

    override fun watchGeneratedLeadsPerTenantTagPerMonth(): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchGeneratedLeadsPerTenantTagPerMonth() }

    override fun watchCountLeadsByContactChannelType(): AppResult<Flow<List<LeadContactChannelCountCompound>>, DataError> =
        onDatabaseSafe { dao.watchCountLeadsByContactChannelType() }

    override fun watchNewStayBookingsPerMonth(): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerMonth() }

    override fun watchNewStayBookingsPerTenantTagPerMonth(): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTenantTagPerMonth() }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchNewStayBookingsPerTop3NeighborhoodsPerMonth() }

    override fun watchCommissionInvoicePerMonth(): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerMonth() }

    override fun watchCommissionInvoicePerTenantTagPerMonth(): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTenantTagPerMonth() }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchCommissionInvoicePerTop3NeighborhoodsPerMonth() }

    override fun watchAverageTicketPerMonth(): AppResult<Flow<List<AverageTicketPerMonthCompound>>, DataError> =
        onDatabaseSafe { dao.watchAverageTicketPerMonth() }
}
