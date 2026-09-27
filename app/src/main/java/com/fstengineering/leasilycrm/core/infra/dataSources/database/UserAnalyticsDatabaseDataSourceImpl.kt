package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.UserAnalyticsDao
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
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.UserAnalyticsDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow

class UserAnalyticsDatabaseDataSourceImpl(
    private val dao: UserAnalyticsDao,
) : UserAnalyticsDatabaseDataSource {
    override fun watchGeneratedLeadsPerMonth(): AppResult<Flow<List<GeneratedLeadsPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchGeneratedLeadsPerMonth() }

    override fun watchGeneratedLeadsPerTenantTagPerMonth(): AppResult<Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchGeneratedLeadsPerTenantTagPerMonth() }

    override fun watchCountLeadsByContactChannelType(): AppResult<Flow<List<LeadContactChannelCountCompound>>, InfraError> =
        onDatabase { dao.watchCountLeadsByContactChannelType() }

    override fun watchNewStayBookingsPerMonth(): AppResult<Flow<List<NewStayBookingsPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchNewStayBookingsPerMonth() }

    override fun watchNewStayBookingsPerTenantTagPerMonth(): AppResult<Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchNewStayBookingsPerTenantTagPerMonth() }

    override fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchNewStayBookingsPerTop3NeighborhoodsPerMonth() }

    override fun watchCommissionInvoicePerMonth(): AppResult<Flow<List<CommissionInvoicePerMonthCompound>>, InfraError> =
        onDatabase { dao.watchCommissionInvoicePerMonth() }

    override fun watchCommissionInvoicePerTenantTagPerMonth(): AppResult<Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchCommissionInvoicePerTenantTagPerMonth() }

    override fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(): AppResult<Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchCommissionInvoicePerTop3NeighborhoodsPerMonth() }

    override fun watchAverageTicketPerMonth(): AppResult<Flow<List<AverageTicketPerMonthCompound>>, InfraError> =
        onDatabase { dao.watchAverageTicketPerMonth() }
}
