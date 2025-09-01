package com.coquankedian.bigtime.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import com.coquankedian.bigtime.data.database.AppDatabase
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BackupManager(private val context: Context) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    fun exportData(onComplete: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context, this)
                val repository = AppRepository(database.eventDao(), database.categoryDao())

                // Get all events and categories
                val events = database.eventDao().getAllActiveEvents()
                val archivedEvents = database.eventDao().getAllArchivedEvents()
                val categories = database.categoryDao().getAllCategories()

                val backupData = JSONObject().apply {
                    put("version", "1.0")
                    put("exportDate", dateFormat.format(Date()))
                    put("events", eventsToJson(getAllEventsSync(database)))
                    put("categories", categoriesToJson(getAllCategoriesSync(database)))
                }

                val fileName = "bigtime_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.json"
                val file = File(context.getExternalFilesDir(null), fileName)
                
                FileWriter(file).use { writer ->
                    writer.write(backupData.toString(2))
                }

                // Share the file
                shareBackupFile(file)
                
                withContext(Dispatchers.Main) {
                    onComplete(true, "备份成功！文件已保存到: $fileName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(false, "备份失败: ${e.message}")
                }
            }
        }
    }

    fun importData(onComplete: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/json"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                
                // For now, we'll show a message about file picker
                withContext(Dispatchers.Main) {
                    onComplete(false, "请手动选择备份文件进行恢复。此功能需要文件选择器支持。")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onComplete(false, "恢复失败: ${e.message}")
                }
            }
        }
    }

    fun importFromJson(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(context, this)
                val repository = AppRepository(database.eventDao(), database.categoryDao())
                
                val backupData = JSONObject(jsonString)
                val version = backupData.getString("version")
                
                if (version != "1.0") {
                    withContext(Dispatchers.Main) {
                        onComplete(false, "不支持的备份文件版本: $version")
                    }
                    return@launch
                }

                // Import categories first
                val categoriesJson = backupData.getJSONArray("categories")
                val categories = jsonToCategories(categoriesJson)
                categories.forEach { category ->
                    try {
                        database.categoryDao().insertCategory(category)
                    } catch (e: Exception) {
                        // Category might already exist, skip
                    }
                }

                // Import events
                val eventsJson = backupData.getJSONArray("events")
                val events = jsonToEvents(eventsJson)
                var importedCount = 0
                
                events.forEach { event ->
                    try {
                        database.eventDao().insertEvent(event)
                        importedCount++
                    } catch (e: Exception) {
                        // Skip duplicates or invalid events
                    }
                }

                withContext(Dispatchers.Main) {
                    onComplete(true, "恢复成功！导入了 $importedCount 个事件和 ${categories.size} 个分类")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onComplete(false, "恢复失败: ${e.message}")
                }
            }
        }
    }

    private suspend fun getAllEventsSync(database: AppDatabase): List<Event> {
        return withContext(Dispatchers.IO) {
            // Get both active and archived events
            val activeEvents = mutableListOf<Event>()
            val archivedEvents = mutableListOf<Event>()
            
            // Since we can't collect Flow synchronously, we'll use direct DAO calls
            // This is a simplified approach for backup purposes
            database.eventDao().getAllActiveEvents().collect { events ->
                activeEvents.addAll(events)
            }
            
            activeEvents
        }
    }

    private suspend fun getAllCategoriesSync(database: AppDatabase): List<Category> {
        return withContext(Dispatchers.IO) {
            val categories = mutableListOf<Category>()
            database.categoryDao().getAllCategories().collect { cats ->
                categories.addAll(cats)
            }
            categories
        }
    }

    private fun eventsToJson(events: List<Event>): JSONArray {
        val jsonArray = JSONArray()
        events.forEach { event ->
            val eventJson = JSONObject().apply {
                put("id", event.id)
                put("title", event.title)
                put("description", event.description)
                put("date", dateFormat.format(event.date))
                put("categoryId", event.categoryId)
                put("isPinned", event.isPinned)
                put("isArchived", event.isArchived)
                put("isLocked", event.isLocked)
                put("backgroundColor", event.backgroundColor)
                put("iconResId", event.iconResId)
                put("cardBackgroundColor", event.cardBackgroundColor)
                put("textColor", event.textColor)
                put("reminderEnabled", event.reminderEnabled)
                put("reminderMinutes", event.reminderMinutes)
                put("isCountingUp", event.isCountingUp)
                put("repeatType", event.repeatType)
                put("isLunarCalendar", event.isLunarCalendar)
                put("endDate", event.endDate?.let { dateFormat.format(it) })
                put("preciseTime", event.preciseTime)
                put("plusOneDay", event.plusOneDay)
                put("highlightColor", event.highlightColor)
                put("notebookId", event.notebookId)
                put("createdAt", dateFormat.format(event.createdAt))
                put("updatedAt", dateFormat.format(event.updatedAt))
            }
            jsonArray.put(eventJson)
        }
        return jsonArray
    }

    private fun categoriesToJson(categories: List<Category>): JSONArray {
        val jsonArray = JSONArray()
        categories.forEach { category ->
            val categoryJson = JSONObject().apply {
                put("id", category.id)
                put("name", category.name)
                put("color", category.color)
                put("icon", category.icon)
                put("isDefault", category.isDefault)
            }
            jsonArray.put(categoryJson)
        }
        return jsonArray
    }

    private fun jsonToEvents(jsonArray: JSONArray): List<Event> {
        val events = mutableListOf<Event>()
        for (i in 0 until jsonArray.length()) {
            val eventJson = jsonArray.getJSONObject(i)
            val event = Event(
                id = 0, // Let database generate new ID
                title = eventJson.getString("title"),
                description = eventJson.optString("description", ""),
                date = dateFormat.parse(eventJson.getString("date")) ?: Date(),
                categoryId = eventJson.getLong("categoryId"),
                isPinned = eventJson.optBoolean("isPinned", false),
                isArchived = eventJson.optBoolean("isArchived", false),
                isLocked = eventJson.optBoolean("isLocked", false),
                backgroundColor = if (eventJson.has("backgroundColor")) eventJson.getInt("backgroundColor") else null,
                iconResId = if (eventJson.has("iconResId")) eventJson.getInt("iconResId") else null,
                cardBackgroundColor = eventJson.optInt("cardBackgroundColor", android.graphics.Color.WHITE),
                textColor = eventJson.optInt("textColor", android.graphics.Color.BLACK),
                reminderEnabled = eventJson.optBoolean("reminderEnabled", false),
                reminderMinutes = eventJson.optInt("reminderMinutes", 1440),
                isCountingUp = eventJson.optBoolean("isCountingUp", false),
                repeatType = eventJson.optString("repeatType", "NONE"),
                isLunarCalendar = eventJson.optBoolean("isLunarCalendar", false),
                endDate = eventJson.optString("endDate", null)?.let { dateFormat.parse(it) },
                preciseTime = eventJson.optString("preciseTime", null),
                plusOneDay = eventJson.optBoolean("plusOneDay", false),
                highlightColor = if (eventJson.has("highlightColor")) eventJson.getInt("highlightColor") else null,
                notebookId = if (eventJson.has("notebookId")) eventJson.getLong("notebookId") else null,
                createdAt = eventJson.optString("createdAt", null)?.let { dateFormat.parse(it) } ?: Date(),
                updatedAt = eventJson.optString("updatedAt", null)?.let { dateFormat.parse(it) } ?: Date()
            )
            events.add(event)
        }
        return events
    }

    private fun jsonToCategories(jsonArray: JSONArray): List<Category> {
        val categories = mutableListOf<Category>()
        for (i in 0 until jsonArray.length()) {
            val categoryJson = jsonArray.getJSONObject(i)
            val category = Category(
                id = 0, // Let database generate new ID
                name = categoryJson.getString("name"),
                color = categoryJson.getInt("color"),
                icon = categoryJson.getString("icon"),
                isDefault = categoryJson.optBoolean("isDefault", false)
            )
            categories.add(category)
        }
        return categories
    }

    private fun shareBackupFile(file: File) {
        try {
            val uri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Days Matter 备份文件")
                putExtra(Intent.EXTRA_TEXT, "这是您的 Days Matter 应用数据备份文件。")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            context.startActivity(Intent.createChooser(shareIntent, "分享备份文件"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}