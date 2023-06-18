package com.example.timewise.ui.calendar.day

// Packages required by DayDatasource class
import android.content.Context
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Event
import com.example.timewise.db.EventDao
import com.example.timewise.db.EventTypeDao
import java.time.LocalDate

// DayDatasource class to get the events of a day from the database
class DayDatasource(private val context: Context) {
    // Database variables
    private lateinit var db: AppDatabase
    private lateinit var eventDao: EventDao
    private lateinit var eventTypeDao: EventTypeDao

    // Get the events of the day passed as a parameter from the database
    // The result is a map with event type as key and event list as value
    fun getEventListOf(dateEventDay: LocalDate): MutableMap<String, List<Event>> {
        // Read events from database
        db = AppDatabase.getInstance(context)
        eventDao = db.eventDao()
        eventTypeDao = db.eventTypeDao()

        // Get all event types
        val eventTypeList = eventTypeDao.getAll()

        // Save in a map with event type as key and event list as value
        val eventDayMap = mutableMapOf<String, List<Event>>()
        eventTypeList.forEach { eventType ->
            // Get event list of the day and event type
            val eventList = eventDao.findByDateAndEventType(dateEventDay, eventType.id)
            // Save in map
            eventDayMap[eventType.name] = eventList
        }

        // Return the event map
        return eventDayMap
    }
}