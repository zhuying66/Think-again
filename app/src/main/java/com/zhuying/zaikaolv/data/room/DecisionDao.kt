package com.zhuying.zaikaolv.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
    @Query("SELECT * FROM decision_records ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<DecisionRecordEntity>>

    @Query("SELECT * FROM decision_records WHERE id = :id")
    suspend fun byId(id: Long): DecisionRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DecisionRecordEntity): Long

    @Delete
    suspend fun delete(record: DecisionRecordEntity)

    @Query("DELETE FROM decision_records")
    suspend fun clear()
}
