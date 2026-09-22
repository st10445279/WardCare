package com.wardcare.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.wardcare.app.R
import com.wardcare.app.data.local.AppDatabase
import com.wardcare.app.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.registerFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.bottomNavigation.visibility = View.GONE
                    binding.syncBanner.visibility = View.GONE
                }
                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
            }
        }

        setupSyncIndicator()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item)
    }

    private fun setupSyncIndicator() {
        val database = AppDatabase.getDatabase(this)
        val medicationDao = database.medicationDao()

        lifecycleScope.launch {
            medicationDao.getPendingCount().collectLatest { count ->
                if (count > 0 && navController.currentDestination?.id !in listOf(R.id.splashFragment, R.id.loginFragment, R.id.registerFragment)) {
                    binding.syncBanner.visibility = View.VISIBLE
                    binding.syncStatusText.text = "$count entries pending sync"
                } else {
                    binding.syncBanner.visibility = View.GONE
                }
            }
        }

        binding.syncNowButton.setOnClickListener {
            triggerManualSync()
        }
    }

    private fun triggerManualSync() {
        val database = AppDatabase.getDatabase(this)
        val repository = com.wardcare.app.data.repository.MedicationRepository(
            database.medicationDao(),
            com.wardcare.app.data.remote.ApiClient.apiService
        )
        lifecycleScope.launch {
            repository.syncPendingLogs()
        }
    }
}
