package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.fragments.Fragment____RecyclerViewForMainActivity
import com.simenko.qmapp.fragments.Fragment______ViewPagerContainer
import com.simenko.qmapp.fragments.Fragment_____NewOrder
import com.simenko.qmapp.viewmodels.QualityManagementViewModel
import com.simenko.qmapp.fragments.Target

class Activity_____Main : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "_____MainActivity"

    val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this) {

        }
        val model = ViewModelProvider(
            this, QualityManagementViewModel.Factory(activity.application)
        ).get(QualityManagementViewModel::class.java)

        model
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolBar
        setSupportActionBar(toolbar)

        this.drawer = binding.drawerLayout

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        toggle.syncState()

        val navigationView: NavigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        if(savedInstanceState == null) {
            this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(0))
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu____filter_top, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                R.id.incoming_inspection -> {
                    TODO("Will filter accordingly")
                }
                R.id.ppap -> {
                    TODO("Will filter accordingly")
                }
                R.id.process_control -> {
                    TODO("Will filter accordingly")
                }
                else -> {
                    TODO("Will filter accordingly")
                }
            }
        } catch (e: Error) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

        return true
    }

    var mPreviousMenuItem: MenuItem? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.setCheckable(true)
        item.setChecked(true)
        if (mPreviousMenuItem != null && mPreviousMenuItem != item) {
            mPreviousMenuItem?.setChecked(false)
        }
        mPreviousMenuItem = item

        val selectedFragment =
            when (item.getItemId()) {
                R.id.nav_team -> Fragment____RecyclerViewForMainActivity.newInstance("Team", Target.TEAM_MEMBERS)
                R.id.nav_structure -> Fragment____RecyclerViewForMainActivity.newInstance("Departments", Target.DEPARTMENTS)
                R.id.nav_products -> Fragment____RecyclerViewForMainActivity.newInstance("Sub departments", Target.SUB_DEPARTMENTS)
                R.id.nav_inv_orders_general -> Fragment______ViewPagerContainer()
                R.id.nav_new_order -> Fragment_____NewOrder()
                else -> Fragment____RecyclerViewForMainActivity.newInstance("Departments", Target.DEPARTMENTS)
            }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, selectedFragment).commit()
//        ToDo better to not replace but delete and add
        val fragments: List<Fragment> = supportFragmentManager.fragments
        fragments.forEach {
            Log.d(TAG, "AlreadyCreatedFragment: $it")
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}

