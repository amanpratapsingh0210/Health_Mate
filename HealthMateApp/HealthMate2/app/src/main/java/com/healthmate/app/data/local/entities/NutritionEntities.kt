package com.healthmate.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "nutrition_logs")
data class NutritionLogEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val date: Long,
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0
)

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = NutritionLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["nutritionLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("nutritionLogId")]
)
data class MealEntity(
    @PrimaryKey
    val id: String,
    val nutritionLogId: String,
    val type: String, // Breakfast, Lunch, Dinner, Snack
    val time: Long
)

@Entity(
    tableName = "food_entries",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mealId")]
)
data class FoodEntryEntity(
    @PrimaryKey
    val id: String,
    val mealId: String,
    val name: String,
    val servingSize: Double,
    val servingUnit: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
) 