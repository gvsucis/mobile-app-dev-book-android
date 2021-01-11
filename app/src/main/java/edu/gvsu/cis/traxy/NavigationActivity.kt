package edu.gvsu.cis.traxy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

class NavigationActivity : AppCompatActivity() {

    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val nc = findNavController(R.id.my_nav_host_fragment)
        appBarConfig = AppBarConfiguration(nc.graph)
        setupActionBarWithNavController(nc, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        val nc = findNavController(R.id.my_nav_host_fragment)
        return nc.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }
}