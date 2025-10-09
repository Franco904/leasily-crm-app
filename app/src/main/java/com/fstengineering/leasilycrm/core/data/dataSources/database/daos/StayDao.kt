package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface StayDao {
    @Upsert
    suspend fun insertOrUpdate(stay: StayEntity): Long

    @Query("UPDATE stays SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): StayCompound

    @Transaction
    @Query("SELECT * FROM stays WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<StayCompound?>

    @Transaction
    @Query("SELECT * FROM stays WHERE user_id = :userId AND is_deleted = 0")
    fun watchAll(userId: Long): Flow<List<StayCompound>>

    @Transaction
    @Query(
        """
        SELECT check_in_date_time, check_out_date_time, is_check_in_done FROM stays
        WHERE is_check_out_done = 0
        AND is_from_other_realtor = 0
        AND is_deleted = 0
        AND user_id = :userId
    """
    )
    fun watchAllUpcomingCheckInsAndCheckOuts(
        userId: Long,
    ): Flow<List<StayCheckStatusCompound>>
}
