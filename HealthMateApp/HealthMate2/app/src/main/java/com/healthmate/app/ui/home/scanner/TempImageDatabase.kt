package com.healthmate.app.ui.home.scanner

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TempImageEntity::class], version = 1, exportSchema = false)
abstract class TempImageDatabase : RoomDatabase() {
    abstract fun tempImageDao(): TempImageDao

    companion object {
        @Volatile
        private var INSTANCE: TempImageDatabase? = null

        fun getInstance(context: Context): TempImageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TempImageDatabase::class.java,
                    "temp_image_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 