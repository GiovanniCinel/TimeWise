package com.example.timewise.ui.home

// Packages required by TaskExpandableListAdapter class
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.timewise.R
import com.example.timewise.db.Task

// TaskExpandableListAdapter class to display the list of tasks
class TaskExpandableListAdapter(private val context: Context) : BaseExpandableListAdapter() {
    // List of tasks
    private var taskList: List<Task> = emptyList()

    // Method to set the list of tasks
    fun setTaskList(taskList: List<Task>) {
        this.taskList = taskList
        notifyDataSetChanged()
    }

    // Method to get the number of groups
    override fun getGroupCount(): Int {
        return taskList.size
    }

    // Method to get the number of children
    override fun getChildrenCount(groupPosition: Int): Int {
        // There are no child elements to display
        return 0
    }

    // Method to get the group
    override fun getGroup(groupPosition: Int): Any {
        return taskList[groupPosition]
    }

    // Method to get the child
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        // There are no child elements to display, so we return a dummy object
        return Any()
    }

    // Method to get the group id
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // Method to get the child id
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // Method to check if the group has stable ids
    override fun hasStableIds(): Boolean {
        return true
    }

    // Method to check if the child is selectable
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    // Method to get the group view
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // Get the task at the specified position
        val task = getGroup(groupPosition) as Task

        // Inflate the view if it is null
        var view = convertView
        if (view == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_group, parent, false)
        }

        // Set the title of the task
        val titleTextView = view?.findViewById<TextView>(R.id.groupTitleTextView)
        titleTextView?.text = task.name
        titleTextView?.gravity = Gravity.CENTER

        // Add the click listener to the group item
        view?.setOnClickListener {
            TaskDialogFragment(task).show(
                (context as FragmentActivity).supportFragmentManager,
                "TaskDialogFragment"
            )
        }

        // Return the view
        return view!!
    }

    // Method to get the child view
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        // There are no child elements to display, so we return an empty view
        return View(context)
    }
}