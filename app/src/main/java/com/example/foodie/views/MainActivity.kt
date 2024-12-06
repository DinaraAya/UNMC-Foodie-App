package com.example.foodie.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.foodie.R
import com.example.foodie.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    public lateinit var binding: ActivityMainBinding

    /**
     * onCreate is called when the activity is first created.
     * Here, we initialize the binding, set the content view, and check if the user is logged in.
     * Based on the login status, we navigate to the appropriate fragment (home or login).
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the NavController from the NavHostFragment to handle navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Check if the user is logged in and navigate accordingly
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(R.id.homeFragment)
        } else {
            navController.navigate(R.id.loginFragment)
        }
        // Setup the navigation drawer
        setupDrawerNavigation(navController)

    }

    /**
     * Opens the navigation drawer.
     */
    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    /**
     * Closes the navigation drawer.
     */
    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    /**
     * Logs out the user from Firebase, navigates to the login fragment, and shows a logout toast message.
     */
    private fun logoutUser() {
        // Sign out the current user from Firebase
        FirebaseAuth.getInstance().signOut()

        // Get the NavController and navigate to the login fragment
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.action_homePageFragment_to_loginFragment)

        // Close the navigation drawer and show a logout confirmation toast
        closeDrawer()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
    }

    /**
     * Sets up the drawer navigation menu and handles item selection.
     * Navigates to the selected fragment and closes the drawer after selection.
     */
    private fun setupDrawerNavigation(navController: androidx.navigation.NavController) {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Navigate to the home fragment
                    navController.navigate(R.id.homeFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_track_order -> {
                    // Navigate to the track order fragment
                    navController.navigate(R.id.trackOrderFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_policy -> {
                    // Navigate to the terms fragment
                    navController.navigate(R.id.termsFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_help -> {
                    // Navigate to the help fragment
                    navController.navigate(R.id.helpFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_faqs -> {
                    // Navigate to the FAQs fragment
                    navController.navigate(R.id.faqsFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_order_history -> {
                    // Navigate to the order history fragment
                    navController.navigate(R.id.orderHistoryFragment)
                    closeDrawer()
                    true
                }
                R.id.nav_logout -> {
                    // Call logoutUser() to sign the user out
                    logoutUser()
                    true
                }
                else -> false // Return false for unhandled items
            }
        }
    }
}




