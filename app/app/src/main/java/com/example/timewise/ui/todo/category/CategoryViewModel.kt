package com.example.timewise.ui.todo.category

// Packages required by CategoryViewModel object
import androidx.fragment.app.Fragment

// CategoryViewModel class containing the instance of the last created fragment and the latest selected category name
object CategoryViewModel {
    lateinit var categoryName: String
    lateinit var fragment: Fragment
}