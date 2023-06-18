package com.example.timewise

// Packages required by MainActivity class
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.timewise.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

// MainActivity class manages app launch
class MainActivity : AppCompatActivity() {
    // Binding object necessary for the app view binding
    private lateinit var binding: ActivityMainBinding

    // Navigation objects for the app
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    // Method for main activity app launch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // App view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Remove app bar in landscape mode for small devices
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            && resources.configuration.screenHeightDp < 600
        ) {
            supportActionBar?.hide()
        }
        // App navigation controller setup
        setupNavController()
    }

    // Method for app navigation controller setup
    private fun setupNavController() {
        // Setup and configuration navigation app bar
        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_calendar, R.id.navigation_todo)
        )

        // Navigation and app bar control coupling
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Setting the fact that double click on an item of the bottom nav bar takes to the first element of the graph
        navView.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.todo_graph -> {
                    navController.popBackStack(R.id.navigation_todo, inclusive = false)
                }
                R.id.calendar_graph -> {
                    navController.popBackStack(R.id.navigation_calendar, inclusive = false)
                }
            }
        }
    }

    // Method to allow app back button working
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}