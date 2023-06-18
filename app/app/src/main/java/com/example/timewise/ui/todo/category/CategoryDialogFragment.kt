package com.example.timewise.ui.todo.category

// Packages required by CategoryDialogFragment class
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.timewise.R
import com.example.timewise.databinding.ModifyCategoryDialogBinding
import com.example.timewise.db.AppDatabase
import com.example.timewise.db.Category
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// CategoryDialogFragment class manages the dialog for category
class CategoryDialogFragment : DialogFragment {
    // Container Fragment must implement this interface
    // It enables the dialog to require an action performed by the container fragment
    interface OnDialogCloseListener {
        fun onDialogClose()
    }

    // CategoryDialogFragment class attributes
    private var fragment: Fragment? = null
    private var mListener: OnDialogCloseListener? = null
    private var categoryName: String

    // Binding section
    private var _binding: ModifyCategoryDialogBinding? = null
    private val binding get() = _binding!!

    // Flag to check if the dialog is recreated by the system
    private var recreated: Boolean

    // This class has two constructors:
    //  - One with categoryName parameter: called when the user click on the "modify" button
    //  - One without parameters: used by the system to recreate the dialog when rotation is performed
    // This is necessary because we need to pass the selected category name to the dialog,
    // but we also need the system to recreate the dialog when rotation is performed

    // Constructor without parameters
    constructor () : super() {
        recreated = true
        categoryName = CategoryViewModel.categoryName
    }

    // Constructor with categoryName parameter
    constructor(categoryName: String) : super() {
        recreated = false
        this.categoryName = categoryName
        // Set the category name in the view model
        CategoryViewModel.categoryName = categoryName
    }

    // Method to create the view of the category dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // Inflate the layout for this fragment
        _binding = ModifyCategoryDialogBinding.inflate(inflater, container, false)

        // Return the root view
        return binding.root
    }

    // Method to create the category dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        // Dialog creation
        val dialog = MaterialAlertDialogBuilder(
            requireContext(),
            R.style.DialogTheme
        ).setTitle(R.string.rename_category).setView(R.layout.modify_category_dialog)
            .setPositiveButton(R.string.accept) { dialog, _ ->
                // Get category name input from edit text
                val modifyCategoryName =
                    (dialog as AlertDialog).findViewById<TextView>(R.id.modify_category_name)?.text
                // Check if category name is empty
                if (!isEmpty(modifyCategoryName)) {
                    // Cast new category name to string
                    val newCategoryName = modifyCategoryName.toString()
                    // Get database instance and category Dao
                    val db = AppDatabase.getInstance(requireContext())
                    val categoryDao = db.categoryDao()
                    // Update category name with new one
                    categoryDao.update(
                        Category(
                            categoryDao.findByCategory(categoryName).id, newCategoryName
                        )
                    )

                    // Overlay label view for category modified
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.modify_category_toast) + ": " + newCategoryName,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.setNegativeButton(R.string.decline, null).create()

        // Set the buttons colors: they need to be set after dialog.show() method
        dialog.setOnShowListener {
            // Get references to positive and negative buttons
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            // Get theme text color
            val textColor = resolveThemeTextColor(requireContext())

            // Set text color for positive and negative buttons
            positiveButton.setTextColor(textColor)
            negativeButton.setTextColor(textColor)

            // If the dialog is recreated by the system, set the category name in the edit text
            if (!recreated) {
                val renameCategoryName = dialog.findViewById<EditText>(R.id.modify_category_name)
                renameCategoryName!!.setText(categoryName)
            }
        }

        // Set the keyboard to not disappear on screen orientation change
        val window = dialog.window
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED)

        // Return the category dialog
        return dialog
    }

    // Method to dismiss the dialog
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        fragment = CategoryViewModel.fragment
        mListener = fragment as OnDialogCloseListener
        mListener!!.onDialogClose()
    }

    // Method to resolve theme text color
    private fun resolveThemeTextColor(context: Context): Int {
        // Resolve theme text color
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
        // Return theme text color
        return typedValue.data
    }
}