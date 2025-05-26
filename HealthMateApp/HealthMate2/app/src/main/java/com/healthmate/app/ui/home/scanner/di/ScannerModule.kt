package com.healthmate.app.ui.home.scanner.di

import android.content.Context
import com.healthmate.app.ui.home.scanner.TempImageDao
import com.healthmate.app.ui.home.scanner.TempImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScannerModule {

    @Provides
    @Singleton
    fun provideTempImageDatabase(@ApplicationContext context: Context): TempImageDatabase {
        return TempImageDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideTempImageDao(database: TempImageDatabase): TempImageDao {
        return database.tempImageDao()
    }
} 