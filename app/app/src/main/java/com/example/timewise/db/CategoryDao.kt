package com.example.timewise.db

// Room package for the database structure
import androidx.room.*

// CategoryDao interface declares the necessary queries to manipulate categories
@Dao
interface CategoryDao {
    // Insert a category into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: Category)

    // Delete categories from database
    @Delete
    fun delete(category: Category)

    // Update categories in the database
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(category: Category)

    // Select all categories from the database
    @Query("SELECT * FROM category_table")
    fun getAll(): List<Category>

    // Select category given its name from the database
    @Query("SELECT * FROM category_table WHERE category_table.name = :name")
    fun findByCategory(name: String): Category

    // Select category given its id from the database
    @Query("SELECT * FROM category_table WHERE category_table.id = :id")
    fun findByCategory(id: Int): Category
}