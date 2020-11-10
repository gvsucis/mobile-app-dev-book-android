package edu.gvsu.cis.traxy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class NavigationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Make action bar accessible to Navigation Controller
        val nc = findNavController(R.id.my_nav_host_fragment)
        setupActionBarWithNavController(nc)
    }
}