package com.example.timewise.ui.home

// Packages required by HomeViewModel object
import com.example.timewise.db.Event
import com.example.timewise.db.Task

// HomeViewModel object to store event and task objects
object HomeViewModel {
    lateinit var event: Event
    lateinit var task: Task
}