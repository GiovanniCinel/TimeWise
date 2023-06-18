package com.example.timewise.ui.todo.task

// Packages required by TaskFullListAdapter class
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Task
import com.example.timewise.db.TaskDao
import com.google.android.material.card.MaterialCardView

// This class inherits from TaskFullListAdapter that accept a parametric type
// It respond to RecyclerView requests
class TaskFullListAdapter(private var taskList: List<Task>) :
    RecyclerView.Adapter<TaskFullListAdapter.TaskViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    // This class knows only the elements type of the RecyclerView but their
    // element is separately specified in R.layout.task_item
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Get references to the task item elements
        private val taskCard: MaterialCardView = itemView.findViewById(R.id.task_item_card)
        private val taskTextView: TextView = itemView.findViewById(R.id.task_name)
        private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.task_description)
        private val imageView: ImageView = itemView.findViewById(R.id.task_priority)

        // Bind task data to the task item elements
        fun bind(task: Task) {
            // Set data to the task item elements
            taskCard.contentDescription = task.id.toString()
            taskTextView.text = task.name
            taskDescriptionTextView.text = task.description

            // Change icon color according to priority
            val iconTintColor = ContextCompat.getColor(
                itemView.context, this@TaskFullListAdapter.getColorFromPriority(task.priority)
            )
            imageView.imageTintList = ColorStateList.valueOf(iconTintColor)
        }
    }

    // Return a CategoryViewHolder reference to the new element
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Display log for each category element created
        Log.d(TAG, "onCreateViewHolder() called")

        // Inflate the task item layout
        // R.layout.task_item specify the element layout for the created ViewHolder
        // retrieving it from the corresponding XML file
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)

        // Get database instance and task Dao
        val db = AppDatabase.getInstance(view.context)
        val taskDao = db.taskDao()

        // Setup modify task button click listener
        taskSetOnClickListener(view, taskDao)

        // Created interface for the new element
        return TaskViewHolder(view)
    }

    // Returns size of task list
    override fun getItemCount(): Int {
        return taskList.size
    }

    // Method to bind holder to specified position
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        // Display log for each category element recycled
        Log.d(TAG, "onBindViewHolder() called")
        // Bind task data to the task item element at the specified position
        holder.bind(taskList[position])
    }

    // Method to setup task click listener
    private fun taskSetOnClickListener(view: View, taskDao: TaskDao) {
        // Setup modify click listener to show modify task dialog
        view.setOnClickListener {
            // Get id from task item card
            val taskId =
                view.findViewById<MaterialCardView>(R.id.task_item_card).contentDescription.toString()
                    .toInt()
            // Get current task from database
            val currentTask = taskDao.findByTask(taskId)

            // Show modify task dialog
            TaskFullListDialogFragment(currentTask).show(
                (view.context as FragmentActivity).supportFragmentManager,
                "TaskFullListDialogFragment"
            )
        }
    }

    // Method to return the color associated to the priority
    private fun getColorFromPriority(priorityId: Int): Int {
        return when (priorityId) {
            1 -> R.color.white_priority
            2 -> R.color.yellow_priority
            3 -> R.color.orange_priority
            4 -> R.color.red_priority
            else -> R.color.white_priority // Default hex for out of bound priority id values
        }
    }

    // Companion object used to save constants
    companion object {
        private val TAG = this::class.simpleName
    }
}