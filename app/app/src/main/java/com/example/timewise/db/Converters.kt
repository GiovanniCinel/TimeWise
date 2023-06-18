package com.example.timewise.db

// Packages required by Converters class
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

// Class containing the converters for the database
// It enables to use Class types, such as LocalDate and LocalTime, in the database
class Converters {

    // Converters for LocalDate
    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    // Converters for LocalTime
    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.toString()
    }
}