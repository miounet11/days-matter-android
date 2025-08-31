package com.coquankedian.bigtime.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: Date, // Target date
    val categoryId: Long,
    val isPinned: Boolean = false, // Pin to top
    val isArchived: Boolean = false, // Archived events
    val isLocked: Boolean = false, // Locked events (need passcode)
    val backgroundColor: Int? = null, // Custom background color
    val iconResId: Int? = null, // Custom icon
    val cardBackgroundColor: Int = android.graphics.Color.parseColor("#FFFFFF"), // Card background color
    val textColor: Int = android.graphics.Color.parseColor("#000000"), // Text color
    val reminderEnabled: Boolean = false,
    val reminderMinutes: Int = 1440, // Default 1 day before
    val isCountingUp: Boolean = false, // false for countdown (future), true for count up (past)
    val repeatType: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY, YEARLY
    val isLunarCalendar: Boolean = false, // Use lunar calendar
    val endDate: Date? = null, // Optional end date for events
    val preciseTime: String? = null, // Precise time in HH:mm format
    val plusOneDay: Boolean = false, // Add one day adjustment
    val highlightColor: Int? = null, // Highlight color for special events
    val notebookId: Long? = null, // Countdown notebook/collection ID
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    // Computed properties
    val daysUntil: Int
        get() {
            val calendar = java.util.Calendar.getInstance()
            val today = calendar.time

            // Reset time to start of day for accurate day calculation
            calendar.time = today
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val todayStart = calendar.time

            calendar.time = date
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            val targetStart = calendar.time

            val diffInMillis = targetStart.time - todayStart.time
            return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
        }

    val daysSince: Int
        get() = -daysUntil

    val isPast: Boolean
        get() = daysUntil < 0

    val isToday: Boolean
        get() = daysUntil == 0

    val isTomorrow: Boolean
        get() = daysUntil == 1

    val isWithinWeek: Boolean
        get() = daysUntil in 1..7

    val isWithinMonth: Boolean
        get() = daysUntil in 1..30

    // Get display text based on date
    val displayText: String
        get() = when {
            isToday -> "今天"
            isTomorrow -> "明天"
            isPast -> "${kotlin.math.abs(daysUntil)}天前"
            daysUntil <= 7 -> "${daysUntil}天后"
            daysUntil <= 30 -> "${daysUntil}天后"
            else -> "${daysUntil}天后"
        }

    val countdownText: String
        get() = when {
            isPast -> "已经过去 ${kotlin.math.abs(daysUntil)} 天"
            isToday -> "就在今天！"
            daysUntil == 1 -> "还有 1 天"
            daysUntil <= 7 -> "还有 ${daysUntil} 天"
            daysUntil <= 30 -> "还有 ${daysUntil} 天"
            else -> "还有 ${daysUntil} 天"
        }

    val statusColor: Int
        get() = when {
            isPast -> android.graphics.Color.parseColor("#757575") // Gray for past events
            isToday -> android.graphics.Color.parseColor("#4CAF50") // Green for today
            daysUntil <= 3 -> android.graphics.Color.parseColor("#FF9800") // Orange for soon
            daysUntil <= 7 -> android.graphics.Color.parseColor("#2196F3") // Blue for this week
            else -> android.graphics.Color.parseColor("#9C27B0") // Purple for future
        }
}
