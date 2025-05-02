package com.healthmate.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.healthmate.app.data.local.entities.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)
    
    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: String): RecipeEntity?
    
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE createdBy = :userId ORDER BY createdAt DESC")
    fun getRecipesByUser(userId: String): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
    
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipe(recipeId: String)
} 