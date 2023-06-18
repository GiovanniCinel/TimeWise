package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// EventTypeDao interface declares the necessary queries to manipulate event types
@Dao
interface EventTypeDao {
    // Insert a event type into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(eventType: EventType)

    // Select all event type from the database
    @Query("SELECT * FROM eventType_table")
    fun getAll(): List<EventType>
}