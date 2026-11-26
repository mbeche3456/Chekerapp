package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckHistoryDao {
  @Query("SELECT * FROM check_history ORDER BY checkedAtMillis DESC")
  fun observeAllHistory(): Flow<List<CheckHistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCheck(entry: CheckHistoryEntity)

  @Query("DELETE FROM check_history WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM check_history")
  suspend fun clearAll()
}
