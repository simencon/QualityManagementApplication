package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.fragments.___DepartmentFragment

class _____MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolBar)
        setSupportActionBar(toolbar)

        this.drawer = findViewById(R.id.drawer_layout)

        val toggle: ActionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu____company_structure_top, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        lateinit var bData: Target
        var bundle: Bundle = Bundle()
        lateinit var nextFragment: Fragment

        when (item.itemId) {
            R.id.departments -> {
                nextFragment = ___DepartmentFragment()
                bData = Target.DEPARTMENTS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
            R.id.team_members -> {
                nextFragment = ___DepartmentFragment()
                bData = Target.TEAM_MEMBERS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
            else -> {
                nextFragment = ___DepartmentFragment()
                bData = Target.DEPARTMENTS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
        }

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, nextFragment).commit()

        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment =
            when (item.getItemId()) {
                R.id.nav_profile -> ___DepartmentFragment()
                else -> ___DepartmentFragment()
            }
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, selectedFragment).commit()

        return true
    }
}

enum class Target(val tList: String) {
    DEPARTMENTS("DEPARTMENTS"),
    TEAM_MEMBERS("TEAM_MEMBERS");

    companion object {
        const val cKey: String = "TARGET_LIST"
    }
}