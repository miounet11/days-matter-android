package com.coquankedian.bigtime.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.coquankedian.bigtime.data.dao.CategoryDao
import com.coquankedian.bigtime.data.dao.EventDao
import com.coquankedian.bigtime.data.dao.NotebookDao
import com.coquankedian.bigtime.data.model.Category
import com.coquankedian.bigtime.data.model.Event
import com.coquankedian.bigtime.data.model.Notebook
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Event::class, Category::class, Notebook::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun categoryDao(): CategoryDao
    abstract fun notebookDao(): NotebookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create notebooks table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `notebooks` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `icon` TEXT NOT NULL DEFAULT '📔',
                        `color` INTEGER NOT NULL DEFAULT -5317653,
                        `coverImage` TEXT,
                        `eventCount` INTEGER NOT NULL DEFAULT 0,
                        `isDefault` INTEGER NOT NULL DEFAULT 0,
                        `isHidden` INTEGER NOT NULL DEFAULT 0,
                        `sortOrder` INTEGER NOT NULL DEFAULT 0,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                """.trimIndent())
                
                // Add notebookId column to events table
                database.execSQL("ALTER TABLE events ADD COLUMN notebookId INTEGER")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "day_counter_database"
                )
                .addCallback(DatabaseCallback(scope))
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.categoryDao(), database.notebookDao())
                }
            }
        }

        suspend fun populateDatabase(categoryDao: CategoryDao, notebookDao: NotebookDao) {
            // Insert default categories
            Category.DEFAULT_CATEGORIES.forEach { category ->
                try {
                    categoryDao.insertCategory(category)
                } catch (e: Exception) {
                    // Category might already exist, skip
                }
            }
            
            // Insert default notebooks
            val defaultNotebooks = listOf(
                Notebook(
                    name = "生活",
                    icon = "🏠",
                    color = android.graphics.Color.parseColor("#4CAF50"),
                    isDefault = true,
                    sortOrder = 0
                ),
                Notebook(
                    name = "工作",
                    icon = "💼", 
                    color = android.graphics.Color.parseColor("#2196F3"),
                    isDefault = true,
                    sortOrder = 1
                ),
                Notebook(
                    name = "纪念日",
                    icon = "🎉",
                    color = android.graphics.Color.parseColor("#FF9800"),
                    isDefault = true,
                    sortOrder = 2
                ),
                Notebook(
                    name = "学习",
                    icon = "📚",
                    color = android.graphics.Color.parseColor("#9C27B0"),
                    isDefault = true,
                    sortOrder = 3
                )
            )
            
            defaultNotebooks.forEach { notebook ->
                try {
                    notebookDao.insertNotebook(notebook)
                } catch (e: Exception) {
                    // Notebook might already exist, skip
                }
            }
        }
    }
}
