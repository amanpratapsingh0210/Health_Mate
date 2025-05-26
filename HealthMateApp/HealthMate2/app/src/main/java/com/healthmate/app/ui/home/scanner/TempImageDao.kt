package com.healthmate.app.ui.home.scanner

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TempImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: TempImageEntity)

    @Query("SELECT * FROM temp_images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<TempImageEntity>>

    @Query("SELECT * FROM temp_images ORDER BY timestamp DESC LIMIT 1")
    fun getLatestImage(): Flow<TempImageEntity?>
} 