package com.coquankedian.bigtime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val color: Int, // Color as integer
    val iconResId: Int, // Icon resource ID
    val isDefault: Boolean = false, // Whether this is a default category
    val isCustom: Boolean = false // Whether this is user-created
) {
    companion object {
        // Default categories
        val ANNIVERSARY = Category(
            name = "纪念日",
            color = android.graphics.Color.parseColor("#FF6B9D"),
            iconResId = android.R.drawable.ic_menu_my_calendar,
            isDefault = true
        )

        val WORK = Category(
            name = "工作",
            color = android.graphics.Color.parseColor("#4ECDC4"),
            iconResId = android.R.drawable.ic_menu_agenda,
            isDefault = true
        )

        val LIFE = Category(
            name = "生活",
            color = android.graphics.Color.parseColor("#45B7D1"),
            iconResId = android.R.drawable.ic_menu_compass,
            isDefault = true
        )

        val DEFAULT_CATEGORIES = listOf(ANNIVERSARY, WORK, LIFE)
    }
}
