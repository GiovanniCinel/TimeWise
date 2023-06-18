package com.example.timewise.db

// Room package for the database structure
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Priority class declares the characteristics of a priority
@Entity(tableName = "priority_table")
data class Priority(
    // Arbitrary integer priority id
    @PrimaryKey val id: Int,
    // Other attributes of the priority
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "name") val name: String
)
