package com.example.timewise.ui.home

// Package required by HomeDatasource class
import android.content.Context
import com.example.timewise.db.*
import java.time.LocalDate

// HomeDatasource class to get data from database
class HomeDatasource(private val context: Context) {
    // Database variables
    private lateinit var db: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var eventDao: EventDao

    // Method to get task list by priority
    fun getTaskByPriority(): List<Task> {
        // Read tasks from database
        db = AppDatabase.getInstance(context)
        taskDao = db.taskDao()

        // Get and return max priority task list
        return taskDao.findByPriority(4)
    }

    // Method to get event list of the specified day
    fun getEventByDay(today: LocalDate): List<Event> {
        // Read events from database
        db = AppDatabase.getInstance(context)
        eventDao = db.eventDao()

        // Get and return event list
        return eventDao.findByDate(today)
    }
}