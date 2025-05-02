package com.healthmate.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.healthmate.app.data.local.converters.StringListConverter

@Entity(tableName = "recipes")
@TypeConverters(StringListConverter::class)
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTime: Int, // in minutes
    val cookTime: Int, // in minutes
    val servings: Int,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val difficulty: String, // EASY, MEDIUM, HARD
    val tags: List<String>,
    val createdBy: String,
    val createdAt: Long
) 