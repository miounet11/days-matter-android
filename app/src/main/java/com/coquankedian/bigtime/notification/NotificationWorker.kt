package com.coquankedian.bigtime.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.coquankedian.bigtime.MainActivity
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.database.AppDatabase
import com.coquankedian.bigtime.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "BIGTIME_REMINDERS"
        const val CHANNEL_NAME = "Days Matter Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for upcoming events"
        const val EVENT_ID_KEY = "event_id"
        const val EVENT_TITLE_KEY = "event_title"
        const val EVENT_DESCRIPTION_KEY = "event_description"
        const val DAYS_UNTIL_KEY = "days_until"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val eventId = inputData.getLong(EVENT_ID_KEY, -1L)
            val eventTitle = inputData.getString(EVENT_TITLE_KEY) ?: "Events Reminder"
            val eventDescription = inputData.getString(EVENT_DESCRIPTION_KEY) ?: ""
            val daysUntil = inputData.getInt(DAYS_UNTIL_KEY, 0)

            // Verify event still exists and is not archived
            val database = AppDatabase.getDatabase(applicationContext, this)
            val repository = AppRepository(database.eventDao(), database.categoryDao())
            val event = repository.getEventById(eventId)

            if (event?.isArchived == false) {
                createNotificationChannel()
                showNotification(eventTitle, eventDescription, daysUntil)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, description: String, daysUntil: Int) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTitle = when {
            daysUntil == 0 -> "今天: $title"
            daysUntil == 1 -> "明天: $title"
            daysUntil > 0 -> "${daysUntil}天后: $title"
            daysUntil == -1 -> "昨天: $title"
            else -> "${Math.abs(daysUntil)}天前: $title"
        }

        val notificationText = when {
            daysUntil == 0 -> "今天就是这个重要的日子！"
            daysUntil > 0 -> "还有${daysUntil}天就到了"
            else -> "已经过去了${Math.abs(daysUntil)}天"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$notificationText\n$description"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}