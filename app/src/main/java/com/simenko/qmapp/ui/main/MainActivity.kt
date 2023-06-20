package com.simenko.qmapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.domain.NoString
import com.simenko.qmapp.domain.OrderTypeProcessOnly
import com.simenko.qmapp.domain.SelectedString
import com.simenko.qmapp.ui.auth.login.LoginActivity
import com.simenko.qmapp.ui.auth.registration.RegistrationActivity
import com.simenko.qmapp.ui.auth.user.UserManager
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingViewModel
import com.simenko.qmapp.ui.main.team.TeamFragment
import com.simenko.qmapp.ui.main.team.TeamViewModel
import com.simenko.qmapp.ui.neworder.*
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriods
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.hilt.android.AndroidEntryPoint
import java.time.Duration
import javax.inject.Inject

internal const val MAIN_KEY_ARG_ORDER_ID = "MAIN_KEY_ARG_ORDER_ID"
internal const val MAIN_KEY_ARG_SUB_ORDER_ID = "MAIN_KEY_ARG_SUB_ORDER_ID"
internal const val REQUEST_PUSH_NOTIFICATIONS_PERMISSION = 1

data class CreatedRecord(
    val orderId: Int = NoRecord.num,
    val subOrderId: Int = NoRecord.num
)

fun setMainActivityResult(
    activity: NewItemActivity,
    actionType: ActionType,
    orderId: Int = NoRecord.num,
    subOrderId: Int = NoRecord.num
) {
    activity.setResult(actionType.ordinal, createMainActivityIntent(activity, orderId, subOrderId))
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

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var userManager: UserManager

    val appModel: ManufacturingViewModel by viewModels()
    val teamModel: TeamViewModel by viewModels()
    val investigationsModel: InvestigationsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    @Inject
    lateinit var workManager: WorkManager
    private lateinit var syncLastHourOneTimeWork: OneTimeWorkRequest
    private lateinit var syncLastDayOneTimeWork: OneTimeWorkRequest
    private lateinit var analytics: FirebaseAnalytics

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQUEST_PUSH_NOTIFICATIONS_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!userManager.isUserLoggedIn()) {
            if (!userManager.isUserRegistered()) {
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_PUSH_NOTIFICATIONS_PERMISSION)
            }

            analytics = Firebase.analytics

            appModel.currentTitle.observe(this) {}

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

            prepareOneTimeWorks()

            if (savedInstanceState == null && intent.extras == null) {
                this.onNavigationItemSelected(navigationView.menu.getItem(0).subMenu!!.getItem(1))
            } else if (intent.extras != null) {
                navigateToProperRecord(bundle = intent.extras)
            }
        }
    }

    private fun prepareOneTimeWorks() {
        syncLastHourOneTimeWork = OneTimeWorkRequestBuilder<SyncEntitiesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setInputData(
                Data.Builder()
                    .putLong(LATEST_MILLIS, SyncPeriods.LAST_HOUR.latestMillis)
                    .putLong(EXCLUDE_MILLIS, SyncPeriods.LAST_HOUR.excludeMillis)
                    .build()
            )
            .setInitialDelay(Duration.ofSeconds(5))
            .build()

        syncLastDayOneTimeWork = OneTimeWorkRequestBuilder<SyncEntitiesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(
                        NetworkType.CONNECTED
                    )
                    .build()
            )
            .setInitialDelay(Duration.ofSeconds(5))
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        navigateToProperRecord(requestCode = requestCode, bundle = intent?.extras)
    }

    private fun navigateToProperRecord(
        requestCode: Int = ActionType.DEFAULT.ordinal,
        bundle: Bundle?
    ) {
        investigationsModel.setCreatedRecord(
            bundle?.getInt(MAIN_KEY_ARG_ORDER_ID) ?: NoRecord.num,
            bundle?.getInt(MAIN_KEY_ARG_SUB_ORDER_ID) ?: NoRecord.num
        )

        when (requestCode) {
            ActionType.ADD_SUB_ORDER_STAND_ALONE.ordinal, ActionType.EDIT_SUB_ORDER_STAND_ALONE.ordinal -> {
                this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(1))
            }

            ActionType.ADD_ORDER.ordinal, ActionType.EDIT_ORDER.ordinal, ActionType.ADD_SUB_ORDER.ordinal, ActionType.EDIT_SUB_ORDER.ordinal -> {
                this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(0))
            }

            ActionType.DEFAULT.ordinal -> {
                this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(0))
            }
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
                investigationsModel.uploadNewInvestigations()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search query text change
                if (investigationsModel.showSubOrderWithOrderType.value == OrderTypeProcessOnly)
                    investigationsModel.setCurrentSubOrdersFilter(
                        number = SelectedString(
                            newText ?: NoString.str
                        )
                    )
                else
                    investigationsModel.setCurrentOrdersFilter(
                        number = SelectedString(
                            newText ?: NoString.str
                        )
                    )
                Log.d(TAG, "onQueryTextChange: $newText")
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

                R.id.upload_master_data -> {
                    investigationsModel.refreshMasterDataFromRepository()
                }

                R.id.sync_investigations -> {
                    workManager.beginUniqueWork(
                        "testWork",
                        ExistingWorkPolicy.KEEP,
                        syncLastHourOneTimeWork
                    )
                        .then(syncLastDayOneTimeWork)
                        .enqueue()
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

        val bundle = Bundle()
        bundle.putString("key1", "Roman")
        bundle.putString("key2", "Semenyshyn")

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
                        analytics.logEvent("nav_company_profile_click", bundle)
                        TODO("Will be fragment to display company profile")
                    }

                    R.id.nav_team -> {
                        analytics.logEvent("nav_team_click", bundle)
                        TeamFragment()
                    }

                    R.id.nav_structure -> {
                        analytics.logEvent("nav_structure_click", bundle)
                        ManufacturingFragment()
                    }

                    R.id.nav_products -> {
                        analytics.logEvent("nav_products_click", bundle)
                        TODO("Will be pager fragment for products")
                    }

                    R.id.nav_inv_orders_general -> {
                        analytics.logEvent("nav_inv_orders_general_click", bundle)
                        investigationsModel.setCurrentSubOrdersFilter(type = NoRecord)
                        InvestigationsFragment()
                    }

                    R.id.nav_inv_orders_process_control -> {
                        analytics.logEvent("nav_inv_orders_process_control_click", bundle)
                        investigationsModel.setCurrentSubOrdersFilter(type = OrderTypeProcessOnly)
                        InvestigationsFragment()
                    }

                    R.id.nav_scrap_level -> {
                        analytics.logEvent("nav_inv_orders_process_control_click", bundle)
                        TODO("Will be monitoring page")
                    }

                    R.id.nav_settings -> {
                        analytics.logEvent("nav_inv_orders_process_control_click", bundle)
                        TODO("Will be settings page")
                    }

                    else -> {
                        analytics.logEvent("nav_inv_orders_process_control_click", bundle)
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
        investigationsModel.hideReportDialog()
        super.onDestroy()
    }
}

