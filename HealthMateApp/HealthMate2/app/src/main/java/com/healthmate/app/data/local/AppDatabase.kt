package com.healthmate.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.healthmate.app.data.local.converters.DateConverter
import com.healthmate.app.data.local.dao.ActivityDao
import com.healthmate.app.data.local.dao.FoodLogDao
import com.healthmate.app.data.local.dao.RecipeDao
import com.healthmate.app.data.local.dao.UserDao
import com.healthmate.app.data.local.entities.ActivityEntity
import com.healthmate.app.data.local.entities.FoodEntryEntity
import com.healthmate.app.data.local.entities.MealEntity
import com.healthmate.app.data.local.entities.NutritionLogEntity
import com.healthmate.app.data.local.entities.RecipeEntity
import com.healthmate.app.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ActivityEntity::class,
        NutritionLogEntity::class,
        MealEntity::class,
        FoodEntryEntity::class,
        RecipeEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun activityDao(): ActivityDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "healthmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 