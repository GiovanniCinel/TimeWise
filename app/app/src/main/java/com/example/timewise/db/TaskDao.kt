package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// TaskDao interface declares the necessary queries to manipulate tasks
@Dao
interface TaskDao {
    // Insert a task into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(task: Task)

    // Delete tasks from database
    @Delete
    fun delete(task: Task)

    // Update tasks in the database
    @Update
    fun update(task: Task)

    // Select all tasks from the database
    @Query("SELECT * FROM task_table ORDER BY task_table.priority DESC")
    fun getAll(): List<Task>

    // Select tasks of a specific category from the database
    @Query("SELECT * FROM task_table WHERE task_table.category = :id ORDER BY task_table.priority DESC")
    fun findByCategory(id: Int): List<Task>

    // Select tasks with a specific priority from the database
    @Query("SELECT * FROM task_table WHERE task_table.priority = :id")
    fun findByPriority(id: Int): List<Task>

    // Select task given its id from the database
    @Query("SELECT * FROM task_table WHERE task_table.id = :id")
    fun findByTask(id: Int): Task
}