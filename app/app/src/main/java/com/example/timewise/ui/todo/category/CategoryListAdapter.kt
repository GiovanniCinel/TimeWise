package com.example.timewise.ui.todo.category

// Packages required by CategoryListAdapter class
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.CategoryDao
import com.example.timewise.db.TaskDao
import com.google.android.material.button.MaterialButton

// CategoryListAdapter class that respond to recycler view requests for categories
class CategoryListAdapter(private var categoryList: Array<String>, private val fragment: Fragment) :
    RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    // CategoryViewHolder class describes a category view and its place within the recycler view
    // This class knows only the elements type of the recycler view but their structure
    // is separately specified in R.layout.category_item
    class CategoryViewHolder(categoryView: View) : RecyclerView.ViewHolder(categoryView) {
        // Category name text view of the given category
        private val categoryName: TextView = itemView.findViewById(R.id.category_name)

        // Method to update category view holder
        fun bind(word: String) {
            // Set category name text view
            categoryName.text = word
        }
    }

    // Method that returns a new category view holder for the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        // Display log for each category view holder created
        Log.d(TAG, "onCreateViewHolder() called")

        // Inflate a new view hierarchy from the category_item XML resource
        // The category_item XML resource specify the layout of category view holder
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)

        // Get database instance
        val db = AppDatabase.getInstance(view.context)
        // Get category Dao
        val categoryDao = db.categoryDao()
        // Get task Dao
        val taskDao = db.taskDao()

        // Return category pencil button object by id
        val pencilButton = view.findViewById<MaterialButton>(R.id.category_pencil_button)
        // Return category delete button object by id
        val deleteButton = view.findViewById<MaterialButton>(R.id.category_delete_button)
        // Return category modify button object by id
        val modifyButton = view.findViewById<MaterialButton>(R.id.category_modify_button)

        // Setup category click listener to a show the corresponding task fragment
        categorySetOnClickListener(view, categoryDao)
        // Setup category pencil button click listener to show/hide modify and delete options
        categoryPencilSetOnClickListener(pencilButton, deleteButton, modifyButton)
        // Setup delete category button click listener
        categoryDeleteSetOnClickListener(parent, view, categoryDao, taskDao, deleteButton)
        // Setup modify category button click listener
        categoryModifySetOnClickListener(view, modifyButton)

        // Return category interface for the recycler view
        return CategoryViewHolder(view)
    }

    // Method that set category filtered list as result of search
    @SuppressLint("NotifyDataSetChanged")
    fun setFilterCategoryList(categoryList: Array<String>) {
        // Set category filtered list
        this.categoryList = categoryList
        // Notify data set changed
        notifyDataSetChanged()
    }

    // Method to setup category on click listener
    private fun categorySetOnClickListener(view: View, categoryDao: CategoryDao) {
        // Setup category on click listener to show the corresponding task fragment
        view.setOnClickListener {
            // Get current category name
            val categoryName = view.findViewById<TextView>(R.id.category_name).text.toString()
            // Get current category id
            val categoryId = categoryDao.findByCategory(categoryName).id
            // Go to corresponding task fragment through category id
            val categoryToTaskAction =
                CategoryFragmentDirections.actionNavigationNotificationsToCategoryFragment(
                    categoryId
                )
            view.findNavController().navigate(categoryToTaskAction)
        }
    }

    // Method to setup pencil category button on click listener
    private fun categoryPencilSetOnClickListener(
        pencilButton: Button,
        deleteButton: Button,
        modifyButton: Button
    ) {
        // Setup category pencil button on click listener to show/hide modify and delete options
        pencilButton.setOnClickListener {
            // Show/hide category delete button
            deleteButton.visibility = if (deleteButton.visibility == View.VISIBLE)
                View.GONE else View.VISIBLE

            // Show/hide category modify button
            modifyButton.visibility = if (modifyButton.visibility == View.VISIBLE)
                View.GONE else View.VISIBLE
        }
    }

    // Method to setup delete category button on click listener
    private fun categoryDeleteSetOnClickListener(
        parent: ViewGroup,
        view: View,
        categoryDao: CategoryDao,
        taskDao: TaskDao,
        deleteButton: Button
    ) {
        // Setup delete category button on click listener
        deleteButton.setOnClickListener {
            // Get current category name
            val categoryName = view.findViewById<TextView>(R.id.category_name).text.toString()
            // Get current category id
            val category = categoryDao.findByCategory(categoryName)
            // Delete tasks corresponding to the current category from database
            val taskCategory = taskDao.findByCategory(category.id)
            taskCategory.forEach { task ->
                // Task deletion
                taskDao.delete(task)
            }
            // Delete current category from database
            categoryDao.delete(category)
            // Update categories recycler view after category deletion
            val categoryList = CategoryDatasource(view.context).getCategoryList()
            (parent as RecyclerView).adapter = CategoryListAdapter(categoryList, fragment)

            // Show delete category overlay label
            Toast.makeText(
                view.context,
                view.context.getString(R.string.delete_category_toast) + ": " + categoryName,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Method to setup modify category button on click listener
    private fun categoryModifySetOnClickListener(
        view: View,
        modifyButton: Button
    ) {
        // Setup modify category button click listener
        modifyButton.setOnClickListener {
            val categoryName = view.findViewById<TextView>(R.id.category_name).text.toString()
            // Create category dialog fragment
            val categoryDialogFragment = CategoryDialogFragment(categoryName)
            // Show category dialog fragment
            categoryDialogFragment.show(
                (view.context as FragmentActivity).supportFragmentManager,
                "CategoryDialogFragment"
            )
        }
    }

    // Method to return the size of the category recycler view
    override fun getItemCount(): Int {
        // Return category list size
        return categoryList.size
    }

    // Method to bind a category view holder in a specific position in the category recycler view
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        // Display log for each category element recycled
        Log.d(TAG, "onBindViewHolder() called")
        // Bind category view holder in the recycler view
        holder.bind(categoryList[position])
    }

    // Companion object used to save constants
    companion object {
        // TAG used to identify class log
        private val TAG = this::class.simpleName
    }
}
