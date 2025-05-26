package com.healthmate.app.ui.home.scanner

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp_images")
data class TempImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val timestamp: Long
) 