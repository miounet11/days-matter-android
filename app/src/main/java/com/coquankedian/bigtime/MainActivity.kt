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
import com.coquankedian.bigtime.databinding.ActivityMainDrawerBinding
import com.coquankedian.bigtime.ui.ArchivedEventFragment
import com.coquankedian.bigtime.ui.CategoryFragment
import com.coquankedian.bigtime.ui.EventListFragment
import com.coquankedian.bigtime.ui.ViewPagerAdapter
import com.coquankedian.bigtime.ui.SettingsActivity
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
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
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
        // Get the current EventListFragment and update its filter
        val currentFragment = supportFragmentManager.findFragmentByTag("f${binding.viewPager.currentItem}")
        if (currentFragment is EventListFragment) {
            currentFragment.filterByCategory(categoryId, pinnedOnly)
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