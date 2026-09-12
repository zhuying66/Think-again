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

    /** 修改单条记录的名称(用户自定义) */
    @Query("UPDATE decision_records SET title = :title WHERE id = :id")
    suspend fun updateTitle(id: Long, title: String?)

    /** 删除单条记录 */
    @Query("DELETE FROM decision_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM decision_records")
    suspend fun clear()
}
