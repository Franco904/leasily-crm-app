package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.GeneratedLeadsPerTenantSegmentPerMonthCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.LeadContactChannelCountCompound
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
            cs.name AS customer_segment_name,
            COUNT(*) AS total
        FROM tenants t
        INNER JOIN customer_segments cs ON t.customer_segment_id = cs.id AND cs.is_deleted = 0
        WHERE t.user_id = :userId
            AND t.is_deleted = 0
            AND datetime(t.create_date_time / 1000, 'unixepoch') >= datetime('now', '-12 months')
        GROUP BY month_year, cs.id
    """
    )
    fun watchGeneratedLeadsPerTenantSegmentPerMonth(userId: Long): Flow<List<GeneratedLeadsPerTenantSegmentPerMonthCompound>>

    @Query(
        """
        SELECT
            tcot.name AS lead_contact_channel_name,
            COUNT(*) AS lead_count
        FROM tenants t
        INNER JOIN tenant_contact_origin_types tcot ON t.tenant_contact_origin_type_id = tcot.id
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


    /*
     * Invoicing
     */
}