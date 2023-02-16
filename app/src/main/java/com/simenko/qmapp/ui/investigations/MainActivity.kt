package com.simenko.qmapp.ui.investigations

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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.fragments.Fragment____RecyclerViewForMainActivity
import com.simenko.qmapp.fragments.Fragment______ViewPagerContainer
import com.simenko.qmapp.fragments.Fragment_____NewOrder
import com.simenko.qmapp.ui.QualityManagementViewModel
import com.simenko.qmapp.fragments.Target
import com.simenko.qmapp.usetesting.CustomManager
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var viewModel: QualityManagementViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    @Inject
    lateinit var messageMain: String

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this, providerFactory)[QualityManagementViewModel::class.java]

        var customManager = CustomManager(application as BaseApplication)
        customManager.changeToGlobalMessage()
        Log.d(TAG, "onCreate: message: ${customManager.messageFromCustomManager}")

        var investigationsComponent =
            (application as BaseApplication).appComponent.investigationsComponent().create()
        investigationsComponent.inject(this)
        Log.d(TAG, "onCreate: message: $messageMain")
        Log.d(TAG, "onCreate: viewModel: $viewModel")

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

        if (savedInstanceState == null) {
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

        lateinit var target: Target
        val selectedFragment =
            when (item.getItemId()) {
                R.id.nav_team -> {
                    target = Target.TEAM_MEMBERS
                    Fragment____RecyclerViewForMainActivity.newInstance(
                        "Team",
                        Target.TEAM_MEMBERS
                    )
                }
                R.id.nav_structure -> {
                    target = Target.DEPARTMENTS
                    Fragment____RecyclerViewForMainActivity.newInstance(
                        "Departments",
                        Target.DEPARTMENTS
                    )
                }
                R.id.nav_products -> {
                    target = Target.SUB_DEPARTMENTS
                    Fragment____RecyclerViewForMainActivity.newInstance(
                        "Sub departments",
                        Target.SUB_DEPARTMENTS
                    )
                }
                R.id.nav_inv_orders_general -> {
                    target = Target.ORDERS
                    Fragment______ViewPagerContainer()
                }

                R.id.nav_inv_orders_status_monitoring -> {
                    target = Target.ORDERS
                    Fragment_____NewOrder()
                }
                else -> {
                    target = Target.DEPARTMENTS
                    Fragment____RecyclerViewForMainActivity.newInstance(
                        "Departments",
                        Target.DEPARTMENTS
                    )
                }
            }
        this.title = target.title

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

