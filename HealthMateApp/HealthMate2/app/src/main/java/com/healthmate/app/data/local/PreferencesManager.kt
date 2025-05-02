package com.healthmate.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "health_mate_preferences")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore
    
    private val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    
    val currentUserId: Flow<String> = dataStore.data.map { preferences ->
        preferences[CURRENT_USER_ID] ?: ""
    }
    
    suspend fun setCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }
    
    suspend fun clearCurrentUserId() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
        }
    }
    
    suspend fun getCurrentUserId(): String {
        return currentUserId.first()
    }
} 