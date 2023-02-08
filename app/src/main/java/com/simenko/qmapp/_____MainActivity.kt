package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.fragments.Fragment____Structure
import com.simenko.qmapp.fragments.Fragment______Inv
import com.simenko.qmapp.fragments._____OrderFragment
import com.simenko.qmapp.viewmodels.QualityManagementViewModel
import com.simenko.qmapp.fragments.Target

class _____MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

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

        toggle.syncState()

        val navigationView: NavigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)
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
                R.id.nav_structure -> Fragment____Structure.newInstance("Departments", Target.DEPARTMENTS)
                R.id.nav_team -> Fragment____Structure.newInstance("Team", com.simenko.qmapp.fragments.Target.TEAM_MEMBERS)
                R.id.nav_inv_orders_general -> Fragment______Inv()
                R.id.nav_new_order -> _____OrderFragment()
                else -> Fragment____Structure.newInstance("Departments", Target.DEPARTMENTS)
            }
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, selectedFragment).commit()

        return true
    }
}

