package com.healthmate.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String,
    val duration: Int, // in minutes
    val distance: Double, // in kilometers
    val calories: Int,
    val steps: Int,
    val heartRate: Int, // average heart rate
    val date: Long,
    val notes: String
) 