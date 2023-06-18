package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// Task class declares the characteristics of a task
// Each task refers to a category and a priority
@Entity(
    tableName = "task_table",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category"]
        ),
        ForeignKey(
            entity = Priority::class,
            parentColumns = ["id"],
            childColumns = ["priority"]
        )
    ],
    indices = [
        Index(value = ["category"]),
        Index(value = ["priority"])
    ]
)
data class Task(
    // Auto-generated integer task id
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // Other attributes of the task
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    // Foreign keys of the task
    @ColumnInfo(name = "category") val category: Int,
    @ColumnInfo(name = "priority") val priority: Int
)
