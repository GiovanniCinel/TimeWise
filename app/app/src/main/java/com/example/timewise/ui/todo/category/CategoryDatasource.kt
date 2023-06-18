package com.example.timewise.ui.todo.category

// Packages required by CategoryDatasource class
import android.content.Context
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.CategoryDao

// CategoryDatasource class manages categories data
class CategoryDatasource(private val context: Context) {
    // Database instance
    private lateinit var db: AppDatabase

    // Category Dao for executing queries
    private lateinit var categoryDao: CategoryDao

    // Method to return all categories from the database
    fun getCategoryList(): Array<String> {
        // Get database instance and category Dao
        db = AppDatabase.getInstance(context)
        categoryDao = db.categoryDao()
        // Select all categories from database
        val categoryList = categoryDao.getAll()

        // String array of all category name
        var categoryNameArray = emptyArray<String>()
        categoryList.forEach { category ->
            // Category name append
            categoryNameArray += category.name
        }

        // Return category name array
        return categoryNameArray
    }
}