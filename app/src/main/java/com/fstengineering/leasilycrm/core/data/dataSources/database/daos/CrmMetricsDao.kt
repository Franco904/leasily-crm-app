package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
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
import kotlinx.coroutines.flow.Flow

@Dao
interface CrmMetricsDao {
    /*
     * Leads
     */
    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(create_date_time / 1000, 'unixepoch')) AS month_year,
            COUNT(*) AS total
        FROM tenants
        WHERE user_id = :userId
            AND is_deleted = 0
            AND datetime(create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year
    """
    )
    fun watchGeneratedLeadsPerMonth(userId: Long): Flow<List<GeneratedLeadsPerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(t.create_date_time / 1000, 'unixepoch')) AS month_year,
            t.tag AS tenant_tag_name,
            COUNT(*) AS total
        FROM tenants_read_view t
        WHERE t.user_id = :userId
            AND t.is_deleted = 0
            AND datetime(t.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year, t.tag
    """
    )
    fun watchGeneratedLeadsPerTenantTagPerMonth(userId: Long):
            Flow<List<GeneratedLeadsPerTenantTagPerMonthCompound>>

    @Query(
        """
        SELECT
            tcot.name AS lead_contact_channel_name,
            COUNT(*) AS lead_count
        FROM tenants_read_view t
        LEFT JOIN tenant_contact_origin_types tcot
            ON t.tenant_contact_origin_type_id = tcot.id
            AND tcot.is_active = 1
        WHERE user_id = :userId
            AND status = 0
            AND is_deleted = 0
        GROUP BY tcot.name
    """
    )
    fun watchCountLeadsByContactChannelType(userId: Long): Flow<List<LeadContactChannelCountCompound>>

    /*
     * Bookings
     */
    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(create_date_time / 1000, 'unixepoch')) AS month_year,
            COUNT(*) AS total
        FROM stays
        WHERE user_id = :userId
            AND is_deleted = 0
            AND is_from_other_realtor = 0
            AND datetime(create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year
    """
    )
    fun watchNewStayBookingsPerMonth(userId: Long): Flow<List<NewStayBookingsPerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(s.create_date_time / 1000, 'unixepoch')) AS month_year,
            t.tag AS tenant_tag_name,
            COUNT(*) AS total
        FROM stays s
        INNER JOIN tenants t
            ON s.tenant_id = t.id
            AND t.is_deleted = 0
        WHERE s.user_id = :userId
            AND s.is_deleted = 0
            AND datetime(s.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year, t.tag
    """
    )
    fun watchNewStayBookingsPerTenantTagPerMonth(userId: Long):
            Flow<List<NewStayBookingsPerTenantTagPerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(s.create_date_time / 1000, 'unixepoch')) AS month_year,
            p.neighborhood AS property_neighborhood,
            COUNT(*) AS total
        FROM stays s
        INNER JOIN properties p
            ON s.property_id = p.id
            AND p.is_deleted = 0
        WHERE s.user_id = :userId
            AND s.is_deleted = 0
            AND s.is_from_other_realtor = 0
            AND datetime(s.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year, p.neighborhood
        ORDER BY total DESC
    """
    )
    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth(userId: Long):
            Flow<List<NewStayBookingsPerTop3NeighborhoodsPerMonthCompound>>

    /*
     * Commission invoice
     */
    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(create_date_time / 1000, 'unixepoch')) AS month_year,
            SUM(commission) AS commission_invoice
        FROM stays
        WHERE user_id = :userId
            AND is_deleted = 0
            AND is_from_other_realtor = 0
            AND datetime(create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
            AND commission IS NOT NULL
        GROUP BY month_year
    """
    )
    fun watchCommissionInvoicePerMonth(userId: Long): Flow<List<CommissionInvoicePerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(s.create_date_time / 1000, 'unixepoch')) AS month_year,
            t.tag AS tenant_tag_name,
            SUM(commission) AS commission_invoice
        FROM stays s
        INNER JOIN tenants t
            ON s.tenant_id = t.id
            AND t.is_deleted = 0
        WHERE s.user_id = :userId
            AND s.is_deleted = 0
            AND datetime(s.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
            AND commission IS NOT NULL
        GROUP BY month_year, t.tag
    """
    )
    fun watchCommissionInvoicePerTenantTagPerMonth(userId: Long):
            Flow<List<CommissionInvoicePerTenantTagPerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(s.create_date_time / 1000, 'unixepoch')) AS month_year,
            p.neighborhood AS property_neighborhood,
            SUM(commission) AS commission_invoice
        FROM stays s
        INNER JOIN properties p
            ON s.property_id = p.id
            AND p.is_deleted = 0
        WHERE s.user_id = :userId
            AND s.is_deleted = 0
            AND s.is_from_other_realtor = 0
            AND datetime(s.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
            AND commission IS NOT NULL
        GROUP BY month_year, p.neighborhood
        ORDER BY commission_invoice DESC
    """
    )
    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth(userId: Long):
            Flow<List<CommissionInvoicePerTop3NeighborhoodsPerMonthCompound>>

    @Query(
        """
        SELECT
            strftime('%m-%Y', datetime(create_date_time / 1000, 'unixepoch')) AS month_year,
            ROUND(SUM(commission) * 1.0 / COUNT(*), 2) AS average_ticket
        FROM stays
        WHERE user_id = :userId
            AND is_deleted = 0
            AND is_from_other_realtor = 0
            AND datetime(create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
            AND commission IS NOT NULL
        GROUP BY month_year
    """
    )
    fun watchAverageTicketPerMonth(userId: Long): Flow<List<AverageTicketPerMonthCompound>>
}
