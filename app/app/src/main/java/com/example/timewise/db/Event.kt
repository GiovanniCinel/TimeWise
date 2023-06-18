package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// Package for date and time
import java.time.LocalDate
import java.time.LocalTime

// Event class declares the characteristics of a event
// Each event refers to a event type
@Entity(
    tableName = "event_table",
    foreignKeys = [
        ForeignKey(
            entity = EventType::class,
            parentColumns = ["id"],
            childColumns = ["eventType"]
        )
    ],
    indices = [
        Index(value = ["eventType"])
    ]
)
data class Event(
    // Auto-generated integer event id
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Other attributes of the event
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "place") val place: String?,
    @ColumnInfo(name = "date") val date: LocalDate?,
    @ColumnInfo(name = "dateStart") val dateStart: LocalDate?,
    @ColumnInfo(name = "dateEnd") val dateEnd: LocalDate?,
    @ColumnInfo(name = "time") val time: LocalTime?,
    @ColumnInfo(name = "celebrated") val celebrated: String?,
    // Foreign keys of the event type
    @ColumnInfo(name = "eventType") val eventType: Int
)
