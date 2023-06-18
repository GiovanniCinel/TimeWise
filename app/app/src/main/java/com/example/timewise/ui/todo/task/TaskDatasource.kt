package com.example.timewise.ui.todo.task

// Packages required by TaskDatasource class
import android.content.Context
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.CategoryDao
import com.example.timewise.db.Task
import com.example.timewise.db.TaskDao

// TaskDatasource class manages tasks data
class TaskDatasource(private val context: Context) {
    // Database instance
    private lateinit var db: AppDatabase

    // Task Dao for executing queries
    private lateinit var taskDao: TaskDao

    // Category Dao for executing queries
    private lateinit var categoryDao: CategoryDao

    // Method to return tasks of a specified category from the database
    fun getTaskListOf(category_id: Int): List<Task> {
        // Read tasks from database
        db = AppDatabase.getInstance(context)
        taskDao = db.taskDao()

        // Return task list of specified category
        return taskDao.findByCategory(category_id)
    }

    // Method to return category name corresponding to a specified category id from the database
    fun getCategoryName(category_id: Int): String {
        // Read tasks from database
        db = AppDatabase.getInstance(context)
        categoryDao = db.categoryDao()

        // Get category name corresponding to category id
        val categoryDao = categoryDao.findByCategory(category_id)

        // Return category name
        return categoryDao.name
    }

    // Method to return all tasks from the database
    fun getTaskList(): List<Task> {
        // Read tasks from database
        db = AppDatabase.getInstance(context)
        taskDao = db.taskDao()

        // Return task list
        return taskDao.getAll()
    }
}