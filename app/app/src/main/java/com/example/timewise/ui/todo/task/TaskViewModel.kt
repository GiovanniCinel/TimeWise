package com.example.timewise.ui.todo.task

// Packages required by TaskViewModel object
import androidx.fragment.app.Fragment
import com.example.timewise.db.Task

// TaskViewModel object contains the instance of the last created fragment,
// the id of the last selected category and the current task
object TaskViewModel {
    lateinit var taskFragment: Fragment
    lateinit var taskFullListFragment: Fragment
    var idCategorySelected: Int = -1
    lateinit var currentTask: Task
}