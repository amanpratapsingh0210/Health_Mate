package com.healthmate.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthmate.app.data.local.entities.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)
    
    @Update
    suspend fun updateActivity(activity: ActivityEntity)
    
    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: String): ActivityEntity?
    
    @Query("SELECT * FROM activities WHERE userId = :userId ORDER BY date DESC")
    fun getActivitiesByUser(userId: String): Flow<List<ActivityEntity>>
    
    @Query("SELECT * FROM activities WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getActivitiesByUserAndDateRange(userId: String, startDate: Long, endDate: Long): Flow<List<ActivityEntity>>
    
    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)
} 