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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ActivityMainBinding
import com.simenko.qmapp.domain.NoRecord
import com.simenko.qmapp.ui.main.settings.SettingsFragment
import com.simenko.qmapp.repository.UserRepository
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsFragment
import com.simenko.qmapp.ui.main.investigations.InvestigationsViewModel
import com.simenko.qmapp.ui.main.manufacturing.ManufacturingViewModel
import com.simenko.qmapp.ui.main.team.TeamFragment
import com.simenko.qmapp.ui.user.createLoginActivityIntent
import com.simenko.qmapp.repository.UserErrorState
import com.simenko.qmapp.repository.UserInitialState
import com.simenko.qmapp.repository.UserLoggedInState
import com.simenko.qmapp.repository.UserLoggedOutState
import com.simenko.qmapp.repository.UserAuthoritiesNotVerifiedState
import com.simenko.qmapp.repository.UserNeedToVerifyEmailState
import com.simenko.qmapp.repository.UserRegisteredState
import com.simenko.qmapp.ui.Screen
import com.simenko.qmapp.works.SyncEntitiesWorker
import com.simenko.qmapp.works.SyncPeriods
import com.simenko.qmapp.works.WorkerKeys.EXCLUDE_MILLIS
import com.simenko.qmapp.works.WorkerKeys.LATEST_MILLIS
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

internal const val MAIN_KEY_ARG_ORDER_ID = "MAIN_KEY_ARG_ORDER_ID"
internal const val MAIN_KEY_ARG_SUB_ORDER_ID = "MAIN_KEY_ARG_SUB_ORDER_ID"
internal const val REQUEST_PUSH_NOTIFICATIONS_PERMISSION = 1

data class CreatedRecord(
    val orderId: Int = NoRecord.num,
    val subOrderId: Int = NoRecord.num
)

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
    lateinit var userRepository: UserRepository

    val appModel: ManufacturingViewModel by viewModels()
    val investigationsModel: InvestigationsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    @Inject
    lateinit var workManager: WorkManager
    private lateinit var syncLastHourOneTimeWork: OneTimeWorkRequest
    private lateinit var syncLastDayOneTimeWork: OneTimeWorkRequest
    private lateinit var analytics: FirebaseAnalytics

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.Main) {

            userRepository.getActualUserState().let { state ->
                when (state) {
                    is UserInitialState -> {
                        startActivity(createLoginActivityIntent(this@MainActivity, Screen.LoggedOut.Registration.route))
                        finish()
                    }

                    is UserNeedToVerifyEmailState -> {
                        Log.d(TAG, "onCreate: ${state.msg}")
                        startActivity(createLoginActivityIntent(this@MainActivity, Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)))
                        finish()
                    }

                    is UserAuthoritiesNotVerifiedState -> {
                        Log.d(TAG, "onCreate: ${state.msg}")
                        startActivity(createLoginActivityIntent(this@MainActivity, Screen.LoggedOut.WaitingForValidation.withArgs(state.msg)))
                        finish()
                    }

                    is UserLoggedOutState -> {
                        startActivity(createLoginActivityIntent(this@MainActivity, Screen.LoggedOut.LogIn.route))
                        finish()
                    }

                    is UserLoggedInState -> {
                        if (ActivityCompat
                                .checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }

                        analytics = Firebase.analytics

                        appModel.currentTitle.observe(this@MainActivity) {}

                        binding = ActivityMainBinding.inflate(layoutInflater)
                        setContentView(binding.root)

                        val toolbar: Toolbar = binding.toolBar
                        setSupportActionBar(toolbar)

                        this@MainActivity.drawer = binding.drawerLayout

                        val toggle = ActionBarDrawerToggle(
                            this@MainActivity, drawer, toolbar,
                            R.string.navigation_drawer_open, R.string.navigation_drawer_close
                        )
                        drawer.addDrawerListener(toggle)

                        toggle.syncState()

                        navigationView = binding.navView
                        navigationView.setNavigationItemSelectedListener(this@MainActivity)

                        prepareOneTimeWorks()

                        if (savedInstanceState == null && intent.extras == null) {
                            this@MainActivity.onNavigationItemSelected(navigationView.menu.getItem(0).subMenu!!.getItem(1))
                        } else if (intent.extras != null) {
                            navigateToProperRecord(bundle = intent.extras)
                        }
                    }

                    is UserErrorState, is UserRegisteredState -> {}
                }
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
        requestCode: Int = AddEditMode.NO_MODE.ordinal,
        bundle: Bundle?
    ) {
        when (requestCode) {
            AddEditMode.ADD_SUB_ORDER_STAND_ALONE.ordinal, AddEditMode.EDIT_SUB_ORDER_STAND_ALONE.ordinal -> {
                this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(1))
            }

            AddEditMode.ADD_ORDER.ordinal, AddEditMode.EDIT_ORDER.ordinal, AddEditMode.ADD_SUB_ORDER.ordinal, AddEditMode.EDIT_SUB_ORDER.ordinal -> {
                this.onNavigationItemSelected(navigationView.menu.getItem(1).subMenu!!.getItem(0))
            }

            AddEditMode.NO_MODE.ordinal -> {
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

                R.id.proper_ui -> {
                    launchMainActivityCompose(this, NoRecord.num)
                }

                R.id.upload_master_data -> {
                    TODO("Will filter accordingly")
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
                        InvestigationsFragment()
                    }

                    R.id.nav_scrap_level -> {
                        analytics.logEvent("nav_scrap_level_click", bundle)
                        TODO("Will be monitoring page")
                    }

                    R.id.nav_settings -> {
                        analytics.logEvent("nav_settings_click", bundle)
                        SettingsFragment()
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
        investigationsModel.hideStatusUpdateDialog()
        super.onDestroy()
    }
}

