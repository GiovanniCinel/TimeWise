package com.example.timewise.db

// Room package for the database structure
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Category class declares the characteristics of a category
// Each category has a unique name
@Entity(
    tableName = "category_table",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class Category(
    // Auto-generated integer category id
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Other attributes of the category
    @ColumnInfo(name = "name") val name: String
)
