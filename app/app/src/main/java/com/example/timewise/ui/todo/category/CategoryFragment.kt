package com.example.timewise.ui.todo.category

// Packages required by CategoryFragment class
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.timewise.R
import com.example.timewise.databinding.FragmentTodoBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Category
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// CategoryFragment class manages categories fragment and recycler view
class CategoryFragment : Fragment(), CategoryDialogFragment.OnDialogCloseListener {
    // Properties only valid between onCreateView and onDestroyView
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    // Add category dialog reference
    private var addCategoryDialog: AlertDialog? = null

    // Category recycler view reference
    private lateinit var recyclerView: RecyclerView

    // Method called when the category dialog is closed
    override fun onDialogClose() {
        // Retrieves all categories data from category datasource
        val categoryList = CategoryDatasource(requireContext()).getCategoryList()
        // Categories recycler view binding
        val recyclerView: RecyclerView = binding.categoryList
        recyclerView.adapter = CategoryListAdapter(categoryList, this)
    }

    // Method to save add category dialog instance state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save add category dialog instance state
        if (addCategoryDialog != null) {
            // Save dialog showing attribute
            outState.putBoolean("addCategoryDialog", addCategoryDialog!!.isShowing)

            // Get category name input from the text view
            val addCategoryName = addCategoryDialog!!.findViewById<TextView>(R.id.add_category_name)
            // Save category name input
            outState.putCharSequence("addCategoryName", addCategoryName!!.text)
        }
    }

    // Method to restore add category dialog instance state
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Restore add category dialog instance state
        if (savedInstanceState != null) {
            // Restore add category dialog if it is open
            if (savedInstanceState.getBoolean("addCategoryDialog")) {
                // Restore dialog show
                showAddCategoryDialog()
                // Set dialog saved input
                setSavedInputAddCategoryDialog(savedInstanceState)
            }
        }
    }

    // Method to set saved input add category dialog
    private fun setSavedInputAddCategoryDialog(savedInstanceState: Bundle?) {
        // Get category name input from the text view
        val addCategoryName = addCategoryDialog?.findViewById<TextView>(R.id.add_category_name)
        // Restore category name input
        addCategoryName?.text = savedInstanceState?.getCharSequence("addCategoryName")
    }

    // Method for creating the category view
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Category fragment inflating
        _binding = FragmentTodoBinding.inflate(inflater, container, false)

        // Get root view
        val root: View = binding.root

        // Categories recycler view binding
        recyclerView = bindCategoryRecyclerView(root)

        // Setup add category button click listener
        addCategoryOnClickListener()

        // Setup search category on query text listener
        searchCategoryOnQueryTextListener(recyclerView)

        // Setup task full list button
        setTaskFullListButtonColor(root)

        // Setup full list button on click listener
        setTaskFullListButtonOnClickListener()

        // Set fragment to view model
        CategoryViewModel.fragment = this

        // Return root view
        return root
    }

    // Method to setup full list button color
    private fun setTaskFullListButtonColor(root: View) {
        // Get reference to the button
        val btnTaskFullList: MaterialButton = root.findViewById(R.id.btn_task_full_list)
        // Set text color and stroke color of the button
        val themeColor = resolveThemeTextColorTertiary(root.context)
        btnTaskFullList.setTextColor(themeColor)
        val colorStateList = ColorStateList.valueOf(themeColor)
        btnTaskFullList.strokeColor = colorStateList
    }

    // Method to setup task full list button on click listener
    private fun setTaskFullListButtonOnClickListener() {
        binding.btnTaskFullList.setOnClickListener {
            // Navigate to TaskFullListFragment using NavController
            findNavController().navigate(R.id.action_navigation_todo_to_taskFullListFragment)
        }
    }

    // Method that filter category list according to the query
    private fun filterCategoryList(query: String?, recyclerView: RecyclerView) {
        // Category list from CategoryDatasource
        val categoryList = CategoryDatasource(recyclerView.context).getCategoryList()

        // Check if query is not null
        if (query != null) {
            // Filter category list according to query
            val filteredCategoryList = categoryList.filter { category ->
                category.lowercase().contains(query.lowercase())
            }
            // Set filtered category list to adapter
            (recyclerView.adapter as CategoryListAdapter).setFilterCategoryList(filteredCategoryList.toTypedArray())
        }
    }

    // Method to setup search category on query text listener
    private fun searchCategoryOnQueryTextListener(recyclerView: RecyclerView) {
        binding.searchCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            // Method that call the filter method of the adapter when query submitted
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            // Method that call the filter method of the adapter when text changes
            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter category list according to query
                filterCategoryList(newText, recyclerView)
                return true
            }
        })
    }

    // Method for categories recycler view binding
    private fun bindCategoryRecyclerView(view: View): RecyclerView {
        // Retrieves all categories data from category datasource
        val categoryList = CategoryDatasource(view.context).getCategoryList()
        // Categories recycler view binding
        val recyclerView: RecyclerView = binding.categoryList
        recyclerView.adapter = CategoryListAdapter(categoryList, this)

        // Return category recycler view
        return recyclerView
    }

    // Method to setup add category button on click listener
    private fun addCategoryOnClickListener() {
        // Setup add category button click listener
        binding.fabAddCategory.setOnClickListener {
            // Show add category dialog
            showAddCategoryDialog()
        }
    }

    // Method to show add category dialog
    private fun showAddCategoryDialog() {
        // Create add category dialog
        val addCategoryDialogBuilder =
            MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
                .setTitle(R.string.add_category)
                .setView(R.layout.add_category_dialog)
                // Positive button logic
                .setPositiveButton(R.string.accept) { dialog, _ ->
                    // Get category name input from the text view
                    val addCategoryName =
                        (dialog as AlertDialog).findViewById<TextView>(R.id.add_category_name)
                    // Check if category name is empty
                    if (!isEmpty(addCategoryName)) {
                        // Get database instance and its category Dao
                        val db = AppDatabase.getInstance(binding.root.context)
                        val categoryDao = db.categoryDao()
                        // Cast category name to string
                        val categoryName = addCategoryName?.text.toString()
                        // Insert new category in the database
                        categoryDao.insert(Category(name = categoryName))
                        // Update category recycler view
                        val categoryList =
                            CategoryDatasource(binding.root.context).getCategoryList()
                        recyclerView.adapter = CategoryListAdapter(categoryList, this)
                        // Overlay label view for category added
                        Toast.makeText(
                            context,
                            requireView().context.getString(R.string.add_category_toast) + ": " + categoryName,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Overlay label view for category not added
                        Toast.makeText(context, R.string.no_empty_element, Toast.LENGTH_LONG).show()
                    }
                }
                // Negative button logic
                .setNegativeButton(R.string.decline, null)

        // Create add category dialog
        addCategoryDialog = addCategoryDialogBuilder.create()

        // Show add category dialog with theme
        addCategoryDialog.apply {
            this!!.setOnShowListener {
                val textColor = resolveThemeTextColor(context)
                val positiveButton = getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setTextColor(textColor)
                val negativeButton = getButton(AlertDialog.BUTTON_NEGATIVE)
                negativeButton.setTextColor(textColor)
            }
        }?.show()
    }

    // Method to resolve theme text color
    private fun resolveThemeTextColor(context: Context): Int {
        // Resolve theme text color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        // Return theme text color
        return typedValue.data
    }

    // Method to resolve theme color tertiary
    private fun resolveThemeTextColorTertiary(context: Context): Int {
        // Resolve theme text color tertiary
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorTertiary, typedValue, true)
        // Return theme text color tertiary
        return typedValue.data
    }

    // Method to check if the text view given is empty
    private fun isEmpty(textView: TextView?): Boolean {
        // Return true if text view is empty
        return textView?.text.toString().trim().isEmpty()
    }

    // Method for destroying the category view
    override fun onDestroyView() {
        super.onDestroyView()
        // Set binding to null
        _binding = null
        // Dismiss add category dialog
        addCategoryDialog?.dismiss()
    }
}