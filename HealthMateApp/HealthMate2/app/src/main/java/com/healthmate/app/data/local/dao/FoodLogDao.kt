package com.healthmate.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.healthmate.app.data.local.entities.FoodEntryEntity
import com.healthmate.app.data.local.entities.MealEntity
import com.healthmate.app.data.local.entities.NutritionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodLogDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutritionLog(nutritionLog: NutritionLogEntity): Long
    
    @Update
    suspend fun updateNutritionLog(nutritionLog: NutritionLogEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long
    
    @Update
    suspend fun updateMeal(meal: MealEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntryEntity): Long
    
    @Update
    suspend fun updateFoodEntry(foodEntry: FoodEntryEntity)
    
    @Query("SELECT * FROM nutrition_logs WHERE userId = :userId AND date = :date")
    suspend fun getNutritionLogByDate(userId: String, date: Long): NutritionLogEntity?
    
    @Query("SELECT * FROM nutrition_logs WHERE userId = :userId ORDER BY date DESC")
    fun getNutritionLogsByUser(userId: String): Flow<List<NutritionLogEntity>>
    
    @Query("SELECT * FROM meals WHERE nutritionLogId = :nutritionLogId")
    suspend fun getMealsByNutritionLogId(nutritionLogId: String): List<MealEntity>
    
    @Query("SELECT * FROM food_entries WHERE mealId = :mealId")
    suspend fun getFoodEntriesByMealId(mealId: String): List<FoodEntryEntity>
    
    @Query("DELETE FROM food_entries WHERE id = :foodEntryId")
    suspend fun deleteFoodEntry(foodEntryId: String)
    
    @Query("DELETE FROM meals WHERE id = :mealId")
    suspend fun deleteMeal(mealId: String)
    
    @Query("DELETE FROM nutrition_logs WHERE id = :nutritionLogId")
    suspend fun deleteNutritionLog(nutritionLogId: String)
    
    @Transaction
    suspend fun getNutritionLogWithMealsAndFoodEntries(nutritionLogId: String): NutritionLogWithMealsAndFoodEntries {
        val nutritionLog = getNutritionLogById(nutritionLogId)
            ?: throw IllegalArgumentException("Nutrition log not found")
        
        val meals = getMealsByNutritionLogId(nutritionLogId)
        val mealsWithFoodEntries = meals.map { meal ->
            val foodEntries = getFoodEntriesByMealId(meal.id)
            MealWithFoodEntries(meal, foodEntries)
        }
        
        return NutritionLogWithMealsAndFoodEntries(nutritionLog, mealsWithFoodEntries)
    }
    
    @Query("SELECT * FROM nutrition_logs WHERE id = :nutritionLogId")
    suspend fun getNutritionLogById(nutritionLogId: String): NutritionLogEntity?
}

data class MealWithFoodEntries(
    val meal: MealEntity,
    val foodEntries: List<FoodEntryEntity>
)

data class NutritionLogWithMealsAndFoodEntries(
    val nutritionLog: NutritionLogEntity,
    val meals: List<MealWithFoodEntries>
) 