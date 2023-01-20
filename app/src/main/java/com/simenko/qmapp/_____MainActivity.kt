package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.simenko.qmapp.fragments.___ProfileFragment

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment =
        when(item.getItemId()) {
            R.id.nav_profile -> ___ProfileFragment()
            else -> ___ProfileFragment()
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

        return true
    }
}