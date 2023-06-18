package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// PriorityDao interface declares the necessary queries to manipulate priorities
@Dao
interface PriorityDao {
    // Insert a priority into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(priority: Priority)

    // Select all priorities from the database
    @Query("SELECT * FROM priority_table")
    fun getAll(): List<Priority>
}