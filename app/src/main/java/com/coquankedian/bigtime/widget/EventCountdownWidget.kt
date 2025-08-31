package com.coquankedian.bigtime.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.coquankedian.bigtime.MainActivity
import com.coquankedian.bigtime.R
import com.coquankedian.bigtime.data.database.AppDatabase
import com.coquankedian.bigtime.data.model.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.filter

import java.text.SimpleDateFormat
import java.util.*

class EventCountdownWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Update each widget instance
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Widget is enabled
    }

    override fun onDisabled(context: Context) {
        // Widget is disabled
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Launch a coroutine to get the next event
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = AppDatabase.getDatabase(context, this)
                    val eventDao = database.eventDao()
                    val nextEvent = getNextEvent(eventDao)

                    withContext(Dispatchers.Main) {
                        val views = createWidgetView(context, nextEvent)

                        // Create pending intent to open the app
                        val intent = Intent(context, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            context, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                } catch (e: Exception) {
                    // Handle error - show default message
                    withContext(Dispatchers.Main) {
                        val views = RemoteViews(context.packageName, R.layout.widget_event_countdown)
                        views.setTextViewText(R.id.tv_widget_title, "Day Counter")
                        views.setTextViewText(R.id.tv_widget_countdown, "暂无事件")
                        appWidgetManager.updateAppWidget(appWidgetId, views)
                    }
                }
            }
        }

        private suspend fun getNextEvent(eventDao: com.coquankedian.bigtime.data.dao.EventDao): com.coquankedian.bigtime.data.model.Event? {
            val events = eventDao.getAllActiveEvents().firstOrNull()
            return events?.filter { !it.isPast }?.minBy { it.daysUntil }
        }

        private fun createWidgetView(context: Context, event: com.coquankedian.bigtime.data.model.Event?): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_event_countdown)

            if (event != null) {
                views.setTextViewText(R.id.tv_widget_title, event.title)
                views.setTextViewText(R.id.tv_widget_countdown, event.countdownText)

                val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                views.setTextViewText(R.id.tv_widget_date, dateFormat.format(event.date))

                // Set colors based on urgency
                val textColor = when {
                    event.isToday -> android.graphics.Color.parseColor("#4CAF50")
                    event.daysUntil <= 3 -> android.graphics.Color.parseColor("#FF9800")
                    event.daysUntil <= 7 -> android.graphics.Color.parseColor("#2196F3")
                    else -> android.graphics.Color.parseColor("#FFFFFF")
                }
                views.setTextColor(R.id.tv_widget_countdown, textColor)
            } else {
                views.setTextViewText(R.id.tv_widget_title, "Day Counter")
                views.setTextViewText(R.id.tv_widget_countdown, "暂无事件")
                views.setTextViewText(R.id.tv_widget_date, "")
            }

            return views
        }
    }
}
