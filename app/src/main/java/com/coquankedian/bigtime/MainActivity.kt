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
import com.coquankedian.bigtime.databinding.ActivityMainBinding
import com.coquankedian.bigtime.ui.ArchivedEventFragment
import com.coquankedian.bigtime.ui.CategoryFragment
import com.coquankedian.bigtime.ui.EventListFragment
import com.coquankedian.bigtime.ui.ViewPagerAdapter
import com.coquankedian.bigtime.ui.SettingsActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupViewPager()
        setupFab()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_all_events)
                1 -> getString(R.string.tab_categories)
                2 -> getString(R.string.tab_archived)
                else -> "Tab ${position + 1}"
            }
        }.attach()
    }

    private fun setupFab() {
        binding.fabAddEvent.setOnClickListener {
            val dialog = com.coquankedian.bigtime.ui.dialog.AddEditEventDialog.newInstance()
            dialog.show(supportFragmentManager, "AddEventDialog")
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
}