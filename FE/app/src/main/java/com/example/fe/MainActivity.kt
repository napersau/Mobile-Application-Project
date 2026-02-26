package com.example.fe

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fe.ui.activity.DecksListActivity
import com.example.fe.ui.activity.ProfileActivity
import com.example.fe.ui.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    // Launcher for activities that should return to Home when finished
    private val activityLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // When Profile / Flashcards / Courses activity closes, reset to Home tab
        bottomNavigation.selectedItemId = R.id.navigation_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigation.selectedItemId = R.id.navigation_home
        }

        // Setup bottom navigation listener
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.navigation_documents -> {
                    loadFragment(DocumentsFragment())
                    true
                }
                R.id.navigation_flashcards -> {
                    // Open DecksListActivity directly; return to Home when done
                    activityLauncher.launch(Intent(this, DecksListActivity::class.java))
                    // Keep bottom nav visually on Home while activity is open
                    false
                }
                R.id.navigation_ai -> {
                    loadFragment(AIFragment())
                    true
                }
                R.id.navigation_profile -> {
                    // Open ProfileActivity directly; return to Home when done
                    activityLauncher.launch(Intent(this, ProfileActivity::class.java))
                    // Keep bottom nav visually on Home while activity is open
                    false
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}