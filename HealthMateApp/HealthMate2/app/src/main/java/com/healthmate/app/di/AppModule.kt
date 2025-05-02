package com.healthmate.app.di

import android.content.Context
import com.healthmate.app.data.local.AppDatabase
import com.healthmate.app.data.local.PreferencesManager
import com.healthmate.app.data.local.dao.ActivityDao
import com.healthmate.app.data.local.dao.FoodLogDao
import com.healthmate.app.data.local.dao.RecipeDao
import com.healthmate.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideActivityDao(database: AppDatabase): ActivityDao {
        return database.activityDao()
    }

    @Provides
    @Singleton
    fun provideFoodLogDao(database: AppDatabase): FoodLogDao {
        return database.foodLogDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
} 