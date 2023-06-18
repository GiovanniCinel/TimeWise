package com.example.timewise.db

// Room package for the database structure
import androidx.room.*
import java.time.LocalDate

// EventDao interface declares the necessary queries to manipulate events
@Dao
interface EventDao {
    // Insert a event into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(event: Event)

    // Delete events from database
    @Delete
    fun delete(event: Event)

    // Update events in the database
    @Update
    fun update(event: Event)

    // Select all events from the database
    @Query("SELECT * FROM event_table")
    fun getAll(): List<Event>

    // Select events of a specific event type from the database
    @Query("SELECT * FROM event_table WHERE event_table.eventType = :idEventType")
    fun findByEventType(idEventType: Int): List<Event>

    // Select events of a specific day and event type ordered by time from the database
    @Query("SELECT * FROM event_table WHERE ((event_table.date = :date) OR (:date BETWEEN event_table.dateStart AND event_table.dateEnd)) AND (event_table.eventType = :idEventType) ORDER BY time")
    fun findByDateAndEventType(date: LocalDate, idEventType: Int): List<Event>

    // Select event of a specific day from the database
    @Query("SELECT * FROM event_table WHERE ((event_table.date = :date) OR (:date BETWEEN event_table.dateStart AND event_table.dateEnd)) ORDER BY time") // +
    fun findByDate(date: LocalDate): List<Event>

    // Select the id of the last event inserted
    @Query("SELECT id FROM event_table ORDER BY id DESC LIMIT 1")
    fun getLastId(): Int

    // Select event by id
    @Query("SELECT * FROM event_table WHERE event_table.id = :id")
    fun findById(id: Int): Event
}