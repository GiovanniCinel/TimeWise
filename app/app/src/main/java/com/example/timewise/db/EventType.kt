package com.example.timewise.db

// Room package for the database structure
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// EventType class declares the characteristics of a event type
// Each event type has a unique name
@Entity(
    tableName = "eventType_table",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class EventType(
    // Auto-generated integer event type id
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Other attributes of the event type
    @ColumnInfo(name = "name") val name: String
)