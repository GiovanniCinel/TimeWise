package com.example.timewise.ui.todo.task

// Packages required by TaskFragment class
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.databinding.FragmentTaskBinding
import com.example.timewise.ui.todo.task.TaskViewModel.idCategorySelected
import com.google.android.material.button.MaterialButtonToggleGroup

// TaskFragment class manages tasks fragment and recycler view
class TaskFragment : Fragment(), OnDialogCloseListener {
    // Binding section
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    // Add task dialog reference
    private var addTaskDialog: AlertDialog? = null

    // Task recycler view reference
    private lateinit var recyclerView: RecyclerView

    // Method to restore add task dialog instance state
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Restore add category dialog instance state
        if (savedInstanceState != null) {
            // Restore add category dialog if it is open
            if (savedInstanceState.getBoolean("addTaskDialog")) {
                // Restore dialog show
                showAddTaskDialog()
                // Set dialog saved input
                setSavedInputAddTaskDialog(savedInstanceState)
            }
        }
    }

    // Method to set saved input add category dialog
    private fun setSavedInputAddTaskDialog(savedInstanceState: Bundle?) {
        // Get task name and description input from the text view
        val addTaskName = addTaskDialog?.findViewById<TextView>(R.id.add_task_name)
        val addTaskDescription = addTaskDialog?.findViewById<TextView>(R.id.add_task_description)
        // Restore task name and description input
        addTaskName?.text = savedInstanceState?.getCharSequence("addTaskName")
        addTaskDescription?.text = savedInstanceState?.getCharSequence("addTaskDescription")

        // Get task priority input from toggle group
        val addTaskPriority =
            addTaskDialog?.findViewById<MaterialButtonToggleGroup>(R.id.priority_toggle_group)
        // Restore task priority input
        addTaskPriority?.check(savedInstanceState!!.getInt("addTaskPriority"))
    }

    // Method for creating the task view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Task fragment inflating
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        // Get root view
        val root: View = binding.root

        // Tasks recycler view binding
        recyclerView = bindCategoryRecyclerView(root)

        // Setup add task button click listener
        addTaskOnClickListener()

        // Search task
        searchTaskOnQueryTextListener(recyclerView)

        // Set fragment to view model
        TaskViewModel.taskFragment = this

        // Return root view
        return root
    }

    // Method for tasks recycler view binding
    private fun bindCategoryRecyclerView(view: View): RecyclerView {
        // Extract category id from which start navigation
        val idCategorySelected = TaskFragmentArgs.fromBundle(requireArguments()).category
        TaskViewModel.idCategorySelected = idCategorySelected
        (activity as AppCompatActivity?)!!.supportActionBar!!.title =
            TaskDatasource(view.context).getCategoryName(idCategorySelected)

        // Retrieves data from task datasource and copy task data in a list using TaskDatasource class
        val taskList = TaskDatasource(view.context).getTaskListOf(idCategorySelected)
        val recyclerView = binding.taskList
        recyclerView.adapter = TaskListAdapter(taskList)

        // Return task recycler view
        return recyclerView
    }

    // Method to setup add task button on click listener
    private fun addTaskOnClickListener() {
        // Setup add task button click listener
        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    // Method to setup search task on query text listener
    private fun searchTaskOnQueryTextListener(recyclerView: RecyclerView) {
        binding.searchTask.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Method that call the filter method of the adapter when query submitted
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // Method that call the filter method of the adapter when text changes
            override fun onQueryTextChange(newText: String?): Boolean {
                filterTaskList(newText, idCategorySelected, recyclerView)
                return true
            }
        })
    }

    // Method that filter task list according to the query
    private fun filterTaskList(query: String?, categoryId: Int, recyclerView: RecyclerView) {
        // Task list from TaskDatasource
        val taskList = TaskDatasource(recyclerView.context).getTaskListOf(categoryId)

        // Filter task list according to query
        if (query != null) {
            val filteredTaskList = taskList.filter { task ->
                task.name.lowercase().contains(query.lowercase()) || task.description.lowercase()
                    .contains(query.lowercase())
            }
            (recyclerView.adapter as TaskListAdapter).setFilterTaskList(filteredTaskList)
        }
    }

    // Method to show add task dialog
    private fun showAddTaskDialog() {
        val addTaskDialogFragment = AddTaskDialogFragment()
        addTaskDialogFragment.show(parentFragmentManager, "AddTaskDialogFragment")
    }

    // Method for destroying the task view
    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null
        _binding = null
        // Dismiss add task dialog
        addTaskDialog?.dismiss()
    }

    // Method invoked when dialog is closed
    override fun onDialogClose() {
        val taskList = TaskDatasource(requireContext()).getTaskListOf(idCategorySelected)
        recyclerView.adapter = TaskListAdapter(taskList)
    }
}

