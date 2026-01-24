package com.fstengineering.leasilycrm.core.infra.dataSources.database.views

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.PrimaryKey
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime

@DatabaseView(
    viewName = "tenants_read_view",
    value = """
        SELECT
            base.*,
            CASE
                WHEN base.has_no_stays OR (NOT base.has_upcoming_check_out AND base.has_unfinished_assistance) THEN 0
                WHEN base.has_upcoming_check_out THEN 1
                ELSE 2
            END AS status
        FROM (
            SELECT
            t.*,
            NOT EXISTS (
                SELECT 1 FROM stays s
                WHERE (s.tenant_id = t.id AND s.is_deleted = 0)
            ) AS has_no_stays,
            EXISTS (
                SELECT 1 FROM assistances a
                WHERE (a.tenant_id = t.id AND a.is_deleted = 0)
                AND a.is_finished = 0
            ) AS has_unfinished_assistance,
            EXISTS (
                SELECT 1 FROM stays s
                WHERE (s.tenant_id = t.id AND s.is_deleted = 0)
                AND s.is_check_out_done = 0
            ) AS has_upcoming_check_out
            FROM tenants t
        ) AS base
    """
)
data class TenantReadView(
    @PrimaryKey val id: Long?,
    @ColumnInfo(name = "create_date_time") val createDateTime: AppDateTime,
    @ColumnInfo(name = "update_date_time") val updateDateTime: AppDateTime,
    val tag: String?,
    val name: String,
    val city: String?,
    @ColumnInfo(name = "identity_document") val identityDocument: String?,
    val phone: String?,
    val email: String?,
    val observations: String?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean,
    @ColumnInfo(name = "tenant_contact_origin_type_id") val tenantContactOriginTypeId: Long?,
    @ColumnInfo(name = "user_id") val userId: Long,
    val status: Int,
)
