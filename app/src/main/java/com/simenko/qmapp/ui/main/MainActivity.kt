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
import com.simenko.qmapp.ui.main.investigations.___InvestigationsContainerFragment
import com.simenko.qmapp.ui.main.team.TeamFragment
import com.simenko.qmapp.ui.neworder.PlaceOrderFragment
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

internal const val KEY_ARG_CREATED_ORDER_ID = "KEY_ARG_CREATED_ORDER_ID"
internal const val KEY_ARG_CREATED_SUB_ORDER_ID = "KEY_ARG_CREATED_SUB_ORDER_ID"

data class CreatedRecord(val orderId: Int = 0, val subOrderId: Int = 0)

fun launchMainActivity(context: Context, orderId: Int = 0, subOrderId: Int = 0) {
    context.startActivity(createMainActivityIntent(context, orderId, subOrderId))
}

fun createMainActivityIntent(context: Context, orderId: Int, subOrderId: Int): Intent {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(KEY_ARG_CREATED_ORDER_ID, orderId)
    intent.putExtra(KEY_ARG_CREATED_SUB_ORDER_ID, subOrderId)
    return intent
}

private lateinit var createdRecord: CreatedRecord

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var viewModel: QualityManagementViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as BaseApplication).appComponent.mainComponent().create().inject(this)
        viewModel = ViewModelProvider(this, providerFactory)[QualityManagementViewModel::class.java]

        viewModel.showOrderNumber.observe(this) {
//            do nothing, just observe
        }

        super.onCreate(savedInstanceState)
        createdRecord = CreatedRecord(
            intent.extras?.getInt(KEY_ARG_CREATED_ORDER_ID) ?: 0,
            intent.extras?.getInt(KEY_ARG_CREATED_SUB_ORDER_ID) ?: 0
        )

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
        val searchItem = menu?.findItem(R.id.search)

        val searchView = searchItem?.actionView as SearchView
        searchView.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                viewModel.syncOrders()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                viewModel.showOrderNumber.value = newText?:"0"
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
                    viewModel.refreshDataFromRepository()
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
                        viewModel.showAllInvestigations.value = true
                        ___InvestigationsContainerFragment(createdRecord)
                    }
                    R.id.nav_inv_orders_process_control -> {
                        viewModel.showAllInvestigations.value = false
                        ___InvestigationsContainerFragment(createdRecord)
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
            this.title = item.title.toString()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment).commit()
        } catch (e: Error) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        viewModel.isStatusDialogVisible.value = false
        super.onDestroy()
    }
}

