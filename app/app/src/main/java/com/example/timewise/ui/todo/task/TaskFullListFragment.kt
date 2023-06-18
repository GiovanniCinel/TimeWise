package com.example.timewise.ui.todo.task

// Packages required by TaskFullListFragment object
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.db.Task

// TaskFullListFragment class to display the full list of tasks
class TaskFullListFragment : Fragment(), OnDialogCloseListener {
    // Task recycler view reference
    private lateinit var taskRecyclerView: RecyclerView

    // Task adapter reference
    private lateinit var taskAdapter: TaskFullListAdapter

    // Method for creating the task view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the task view
        val root = inflater.inflate(R.layout.fragment_task_full_list, container, false)

        // Set task recycler view and adapter
        taskRecyclerView = root.findViewById(R.id.taskRecyclerView)
        taskAdapter = TaskFullListAdapter(getTaskList())
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set fragment to view model
        TaskViewModel.taskFullListFragment = this

        // Return view
        return root
    }

    // Method to get task list
    private fun getTaskList(): List<Task> {
        return TaskDatasource(taskRecyclerView.context).getTaskList()
    }

    // Method invoked when dialog is closed
    override fun onDialogClose() {
        taskRecyclerView.adapter = TaskFullListAdapter(getTaskList())
    }
}