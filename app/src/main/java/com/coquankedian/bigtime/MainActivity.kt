package com.coquankedian.bigtime

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.coquankedian.bigtime.databinding.ActivityMainDrawerBinding
import com.coquankedian.bigtime.ui.ArchivedEventFragment
import com.coquankedian.bigtime.ui.CategoryFragment
import com.coquankedian.bigtime.ui.EventListFragment
import com.coquankedian.bigtime.ui.ViewPagerAdapter
import com.coquankedian.bigtime.ui.SettingsActivity
import com.coquankedian.bigtime.ui.DateCalculatorActivity
import com.coquankedian.bigtime.data.backup.BackupManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainDrawerBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private var isGridView = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        // Setup drawer
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView
        
        // Setup navigation drawer
        setupNavigationDrawer()
        setupViewPager()
        setupButtons()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigationDrawer() {
        // Setup drawer toggle
        binding.toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        
        // Setup navigation item selection
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Show pinned events only
                    filterEventsByCategory(null, true)
                }
                R.id.nav_all -> {
                    // Show all events
                    filterEventsByCategory(null, false)
                }
                R.id.nav_life -> {
                    // Filter by life category (ID: 3)
                    filterEventsByCategory(3L, false)
                }
                R.id.nav_work -> {
                    // Filter by work category (ID: 2)
                    filterEventsByCategory(2L, false)
                }
                R.id.nav_anniversary -> {
                    // Filter by anniversary category (ID: 1)
                    filterEventsByCategory(1L, false)
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    
    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        
        // Remove TabLayout since we're using drawer navigation
        binding.viewPager.isUserInputEnabled = true
    }

    private fun setupButtons() {
        // Setup add button
        binding.btnAdd.setOnClickListener {
            val dialog = com.coquankedian.bigtime.ui.dialog.AddEditEventDialog.newInstance()
            dialog.show(supportFragmentManager, "AddEventDialog")
        }
        
        // Setup grid view toggle
        binding.btnGridView.setOnClickListener {
            isGridView = !isGridView
            updateViewMode()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        
        // Setup SearchView
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        
        searchView?.let { sv ->
            sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    performSearch(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        clearSearch()
                    } else {
                        performSearch(newText)
                    }
                    return true
                }
            })
            
            sv.setOnCloseListener {
                clearSearch()
                false
            }
        }
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_date_calculator -> {
                startActivity(Intent(this, DateCalculatorActivity::class.java))
                true
            }
            R.id.action_backup -> {
                performBackup()
                true
            }
            R.id.action_restore -> {
                performRestore()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun updateViewMode() {
        // Update icon based on current mode
        binding.btnGridView.setImageResource(
            if (isGridView) R.drawable.ic_baseline_view_list_24
            else R.drawable.ic_baseline_apps_24
        )
        
        // Notify fragments about view mode change
        supportFragmentManager.fragments.forEach { fragment ->
            if (fragment is EventListFragment) {
                fragment.setViewMode(isGridView)
            }
        }
    }
    
    private fun filterEventsByCategory(categoryId: Long?, pinnedOnly: Boolean) {
        // Get the EventListFragment from the ViewPager
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            // Find the EventListFragment in the ViewPager
            if (fragment is EventListFragment) {
                fragment.filterByCategory(categoryId, pinnedOnly)
                break
            }
        }
    }
    
    private fun performSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            clearSearch()
            return
        }
        
        // Get the EventListFragment and perform search
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is EventListFragment) {
                fragment.searchEvents(query)
                break
            }
        }
    }
    
    private fun clearSearch() {
        // Clear search and return to normal view
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is EventListFragment) {
                fragment.clearSearch()
                break
            }
        }
    }
    
    private fun performBackup() {
        val backupManager = BackupManager(this)
        backupManager.exportData { success, message ->
            runOnUiThread {
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun performRestore() {
        val backupManager = BackupManager(this)
        backupManager.importData { success, message ->
            runOnUiThread {
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
                if (success) {
                    // Refresh the current fragment
                    recreate()
                }
            }
        }
    }
    
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}