package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodListProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodNotificationProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckInCheckOutCountsProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Upsert
    suspend fun insertOrUpdate(stay: StayEntity): Long

    @Query("UPDATE stays SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE stays SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: Int)

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): StayCompound

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<StayCompound?>

    @Query("SELECT * FROM stays WHERE tenant_id = :tenantId AND is_deleted = 0")
    suspend fun getAllByTenantId(tenantId: Long): List<StayEntity>

    @Query("SELECT id FROM stays WHERE tenant_id = :tenantId AND is_deleted = 0 AND (status = 0 OR status = 1) LIMIT 1")
    suspend fun getCurrentStayIdByTenantId(tenantId: Long): Long?

    @Transaction
    @Query("SELECT * FROM stays WHERE is_deleted = 0")
    fun watchAll(): Flow<List<StayCompound>>

    @Query(
        """
        SELECT * FROM stays
        WHERE property_id = :propertyId
        AND is_deleted = 0
        ORDER BY check_in_date_time
        """
    )
    fun watchAllByPropertyId(propertyId: Long): Flow<List<StayEntity>>

    @Query(
        """
        SELECT
            s.id,
            s.tenant_id,
            s.check_in_date_time,
            s.check_out_date_time,
            s.is_auto_check_in,
            s.is_auto_check_out,
            s.status,
            t.name AS tenant_name,
            p.property_type_id,
            p.neighborhood
        FROM stays s
        LEFT JOIN lead_tenants_read_view t ON s.tenant_id = t.id AND t.is_deleted = 0
        LEFT JOIN properties p ON s.property_id = p.id AND p.is_deleted = 0
        WHERE s.is_deleted = 0
    """
    )
    fun watchAllWithDetails(): Flow<List<OccupiedPeriodListProjection>>

    @Query(
        """
        SELECT
            t.name AS tenant_name,
            p.property_type_id AS property_type_id,
            po.name AS property_owner_name,
            s.check_in_date_time,
            s.check_out_date_time,
            s.status,

            CASE 
                WHEN date(s.check_in_date_time / 1000, 'unixepoch') = date(:nowTimestamp / 1000, 'unixepoch', 'localtime')
                THEN 1 ELSE 0
            END AS is_check_in_today,

            CASE 
                WHEN date(s.check_out_date_time / 1000, 'unixepoch') = date(:nowTimestamp / 1000, 'unixepoch', 'localtime')
                THEN 1 ELSE 0
            END AS is_check_out_today

        FROM stays s
        LEFT JOIN lead_tenants_read_view t ON s.tenant_id = t.id AND t.is_deleted = 0
        LEFT JOIN properties p ON s.property_id = p.id AND p.is_deleted = 0
        LEFT JOIN property_owners po ON p.property_owner_id = po.id AND po.is_deleted = 0
        WHERE (s.status = 0 OR s.status = 1)
        AND s.tenant_id IS NOT NULL
        AND t.name IS NOT NULL
        AND p.property_type_id IS NOT NULL
        AND po.name IS NOT NULL
        AND (
            date(s.check_in_date_time / 1000, 'unixepoch', 'localtime') = date(:nowTimestamp / 1000, 'unixepoch', 'localtime')
            OR date(s.check_out_date_time / 1000, 'unixepoch', 'localtime') = date(:nowTimestamp / 1000, 'unixepoch', 'localtime')
        )
        AND s.is_deleted = 0
    """
    )
    fun getAllTodayCheckInsAndCheckOutsToNotify(nowTimestamp: Long): List<OccupiedPeriodNotificationProjection>

    @Query(
        """
        SELECT
        COUNT(
            CASE 
                WHEN s.status = 0 AND :nowStartOfDay >= s.check_in_date_time 
                THEN 1
            END
        ) AS stays_able_to_check_in_count,
        COUNT(
            CASE 
                WHEN s.status < 2 AND :nowStartOfDay >= s.check_out_date_time 
                THEN 1
            END
        ) AS stays_able_to_check_out_count
        FROM stays s
        WHERE is_deleted = 0
    """
    )
    fun watchAllPastCheckInCheckOutNotDoneCounts(nowStartOfDay: AppDateTime): Flow<StayCheckInCheckOutCountsProjection>
}
