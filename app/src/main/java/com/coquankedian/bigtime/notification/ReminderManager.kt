package com.coquankedian.bigtime.notification

import android.content.Context
import androidx.work.*
import com.coquankedian.bigtime.data.model.Event
import java.util.concurrent.TimeUnit

class ReminderManager(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleReminder(event: Event) {
        if (!event.reminderEnabled) {
            cancelReminder(event.id)
            return
        }

        val eventTime = event.date.time
        val currentTime = System.currentTimeMillis()
        val reminderTime = eventTime - (event.reminderMinutes * 60 * 1000L)
        
        // Only schedule if reminder time is in the future
        if (reminderTime <= currentTime) {
            return
        }

        val delay = reminderTime - currentTime
        val daysUntil = ((eventTime - currentTime) / (1000 * 60 * 60 * 24)).toInt()

        val inputData = Data.Builder()
            .putLong(NotificationWorker.EVENT_ID_KEY, event.id)
            .putString(NotificationWorker.EVENT_TITLE_KEY, event.title)
            .putString(NotificationWorker.EVENT_DESCRIPTION_KEY, event.description)
            .putInt(NotificationWorker.DAYS_UNTIL_KEY, daysUntil)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .addTag("reminder_${event.id}")
            .build()

        workManager.enqueueUniqueWork(
            "reminder_${event.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(eventId: Long) {
        workManager.cancelUniqueWork("reminder_$eventId")
    }

    fun rescheduleAllReminders(events: List<Event>) {
        // Cancel all existing reminders
        workManager.cancelAllWorkByTag("reminder")
        
        // Schedule new reminders for all events that have reminders enabled
        events.filter { it.reminderEnabled && !it.isArchived }.forEach { event ->
            scheduleReminder(event)
        }
    }

    fun scheduleRepeatingEvent(originalEvent: Event) {
        if (originalEvent.repeatType == "NONE") return

        val calendar = java.util.Calendar.getInstance()
        calendar.time = originalEvent.date

        // Calculate next occurrence based on repeat type
        when (originalEvent.repeatType) {
            "DAILY" -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> calendar.add(java.util.Calendar.MONTH, 1)
            "YEARLY" -> calendar.add(java.util.Calendar.YEAR, 1)
        }

        val nextDate = calendar.time
        val nextEvent = originalEvent.copy(
            id = 0, // New event
            date = nextDate,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )

        // This would need to be called from a place where we have access to the repository
        // We'll implement this in the EventViewModel instead
    }
}