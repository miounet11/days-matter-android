package com.coquankedian.bigtime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notebooks")
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String = "📔", // Emoji or icon identifier
    val color: Int = android.graphics.Color.parseColor("#4A90E2"),
    val coverImage: String? = null, // Path to cover image
    val eventCount: Int = 0, // Number of events in this notebook
    val isDefault: Boolean = false, // Default notebooks like Life, Work, Anniversary
    val isHidden: Boolean = false, // Hidden notebooks
    val sortOrder: Int = 0, // Display order
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)