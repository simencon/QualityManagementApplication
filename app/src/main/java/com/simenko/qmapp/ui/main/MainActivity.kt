package com.simenko.qmapp.ui.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.team.TeamFragment
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

internal const val MAIN_KEY_ARG_ORDER_ID = "MAIN_KEY_ARG_ORDER_ID"
internal const val MAIN_KEY_ARG_SUB_ORDER_ID = "MAIN_KEY_ARG_SUB_ORDER_ID"

data class CreatedRecord(val orderId: Int = 0, val subOrderId: Int = 0)

fun setMainActivityResult(
    activity: NewItemActivity,
    actionType: Int,
    orderId: Int = 0,
    subOrderId: Int = 0
) {
    activity.setResult(actionType, createMainActivityIntent(activity, orderId, subOrderId))
}

fun createMainActivityIntent(
    context: Context,
    orderId: Int,
    subOrderId: Int
): Intent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(MAIN_KEY_ARG_ORDER_ID, orderId)
    intent.putExtra(MAIN_KEY_ARG_SUB_ORDER_ID, subOrderId)
    return intent
}


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var appModel: QualityManagementViewModel
    lateinit var investigationsModel: InvestigationsViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    var requestCode: Int = -1
    private var createdRecord = CreatedRecord(-1,-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.mainComponent().create().inject(this)
        appModel = ViewModelProvider(this, providerFactory)[QualityManagementViewModel::class.java]
        investigationsModel = ViewModelProvider(this, providerFactory)[InvestigationsViewModel::class.java]

        investigationsModel.showOrderNumber.observe(this) {}
        appModel.currentTitle.observe(this) {}

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolBar
        setSupportActionBar(toolbar)

        this.drawer = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)

        toggle.syncState()

        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            this.onNavigationItemSelected(navigationView.menu.getItem(0).subMenu!!.getItem(1))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        this.requestCode = resultCode

        this.createdRecord = CreatedRecord(
            intent?.extras?.getInt(MAIN_KEY_ARG_ORDER_ID) ?: 0,
            intent?.extras?.getInt(MAIN_KEY_ARG_SUB_ORDER_ID) ?: 0
        )
        investigationsModel.createdRecord.value = createdRecord

        if (
            requestCode == ActionType.ADD_SUB_ORDER_STAND_ALONE.ordinal ||
            requestCode == ActionType.EDIT_SUB_ORDER_STAND_ALONE.ordinal
        ) {
            this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(1))
        } else if(
            requestCode == ActionType.ADD_ORDER.ordinal ||
            requestCode == ActionType.EDIT_ORDER.ordinal ||
            requestCode == ActionType.ADD_SUB_ORDER.ordinal ||
            requestCode == ActionType.EDIT_SUB_ORDER.ordinal
        ) {
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
        val searchItem = menu?.findItem(R.id.search)

        val searchView = searchItem?.actionView as SearchView
        searchView.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                investigationsModel.syncOrders()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                investigationsModel.showOrderNumber.value = newText ?: "0"
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {
            when (item.itemId) {
                R.id.search -> {
//                    is implemented within on search listener
                }
                R.id.upload_all_data -> {
                    investigationsModel.refreshDataFromRepository()
                }
                R.id.ppap -> {
                    TODO("Will filter accordingly")
                }
                R.id.incoming_inspection -> {
                    TODO("Will filter accordingly")
                }
                R.id.process_control -> {
                    TODO("Will filter accordingly")
                }
                R.id.product_audit -> {
                    TODO("Will filter accordingly")
                }
                R.id.custom_filter -> {
                    TODO("Will filter accordingly")
                }
                else -> {
                }
            }
        } catch (e: Error) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

        return true
    }

    var mPreviousMenuItem: MenuItem? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        item.isCheckable = true
        item.isChecked = true
        if (mPreviousMenuItem != null && mPreviousMenuItem != item) {
            mPreviousMenuItem?.isChecked = false
        }
        mPreviousMenuItem = item

        try {
            val selectedFragment =
                when (item.itemId) {
                    R.id.nav_company_profile -> {
                        TODO("Will be fragment to display company profile")
                    }
                    R.id.nav_team -> {
                        TeamFragment()
                    }
                    R.id.nav_structure -> {
                        ManufacturingFragment()
                    }
                    R.id.nav_products -> {
                        TODO("Will be pager fragment for products")
                    }
                    R.id.nav_inv_orders_general -> {
                        investigationsModel.showAllInvestigations.value = true
                        InvestigationsFragment(createdRecord)
                    }
                    R.id.nav_inv_orders_process_control -> {
                        investigationsModel.showAllInvestigations.value = false
                        InvestigationsFragment(createdRecord)
                    }

                    R.id.nav_scrap_level -> {
                        TODO("Will be monitoring page")
                    }
                    R.id.nav_settings -> {
                        PlaceOrderFragment()
                    }
                    else -> {
                        TODO("Will be monitoring page")
                    }
                }
            appModel.currentTitle.value = item.title.toString()
            this.title = item.title.toString()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment).commit()
        } catch (e: Error) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        this.title = appModel.currentTitle.value
    }

    override fun onDestroy() {
        appModel.isStatusDialogVisible.value = false
        super.onDestroy()
    }
}

