package com.example.timewise.db

// Context class referred to global application environment
import android.content.Context

// Room package for the database structure
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// AppDatabase class is abstract and declares the allowed operations by the database
@Database(
    entities = [Category::class, Priority::class, Task::class, EventType::class, Event::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Category Dao for categories manipulation
    abstract fun categoryDao(): CategoryDao

    // Task Dao for tasks manipulation
    abstract fun taskDao(): TaskDao

    // Priority Dao for priorities manipulation
    abstract fun priorityDao(): PriorityDao

    // EventType Dao for event types manipulation
    abstract fun eventTypeDao(): EventTypeDao

    // Event Dao for event manipulation
    abstract fun eventDao(): EventDao

    // Companion object necessary to work with database instances
    companion object {
        // Database instance declaration
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Get method to return database instance
        fun getInstance(context: Context): AppDatabase {
            // Check database instance existence
            if (INSTANCE == null) {
                // Database instance creation
                // The following code is a coroutine that create a database instance
                // A coroutine is a thread that executes code asynchronously
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "timewise_database"
                    )
                        .fallbackToDestructiveMigration() // Wipes and rebuilds database instead of migrating
                        .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.Default))) // Add a callback to populate database
                        .allowMainThreadQueries() // Allow query execution in the main activity
                        .build()
                }
            }

            // Return database instance
            return INSTANCE as AppDatabase
        }

        // Callback to populate database
        private class AppDatabaseCallback(private val scope: CoroutineScope) : Callback() {
            // Override onCreate method to populate database to get data persistence
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Default database population
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.priorityDao(), database.eventTypeDao())
                    }
                }
            }
        }

        // Populate the database with default priorities and event types
        fun populateDatabase(priorityDao: PriorityDao, eventTypeDao: EventTypeDao) {
            // Default priority table initialization
            priorityDao.insert(Priority(1, "white_priority", "low"))
            priorityDao.insert(Priority(2, "yellow_priority", "medium"))
            priorityDao.insert(Priority(3, "orange_priority", "high"))
            priorityDao.insert(Priority(4, "red_priority", "very high"))

            // Default event type table initialization
            eventTypeDao.insert(EventType(1, "Compleanno"))
            eventTypeDao.insert(EventType(2, "Viaggio"))
            eventTypeDao.insert(EventType(3, "Riunione"))
            eventTypeDao.insert(EventType(4, "Appuntamento"))
            eventTypeDao.insert(EventType(5, "Scadenza"))
            eventTypeDao.insert(EventType(6, "Altro"))
        }
    }
}