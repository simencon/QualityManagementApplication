package com.simenko.qmapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simenko.qmapp.fragments.Fragment____Investigations
import com.simenko.qmapp.fragments.Fragment_____Structure
import com.simenko.qmapp.fragments.TargetInv
import com.simenko.qmapp.fragments._____OrderFragment
import com.simenko.qmapp.pagers.OrderSectionPagerAdapter
import com.simenko.qmapp.pagers.ZoomOutPageTransformer
import com.simenko.qmapp.pagers.tabTitles

class _____MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, SendMessage {

    private lateinit var drawer: DrawerLayout

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

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

        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = findViewById(R.id.fragment_container)

        val pagerAdapter = OrderSectionPagerAdapter( this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val tabLayout = findViewById<TabLayout>(R.id.tabs)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TargetInv.values()[position].name
        }.attach()

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
                nextFragment = Fragment_____Structure("Fragment title")
                bData = Target.DEPARTMENTS

                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
            R.id.team_members -> {
                nextFragment = Fragment_____Structure("Fragment title")
                bData = Target.TEAM_MEMBERS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
            R.id.orders -> {
                nextFragment = Fragment_____Structure("Fragment title")
                bData = Target.ORDERS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
            else -> {
                nextFragment = Fragment_____Structure("Fragment title")
                bData = Target.DEPARTMENTS
                bundle.putString(Target.cKey, bData.tList)
                nextFragment.arguments = bundle
            }
        }

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, nextFragment).commit()

        return true
    }

    var mPreviousMenuItem: MenuItem? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

//        Make only one multiples menu items checked
        item.setCheckable(true)
        item.setChecked(true)
        if (mPreviousMenuItem != null && mPreviousMenuItem != item) {
            mPreviousMenuItem?.setChecked(false)
        }
        mPreviousMenuItem = item

        val selectedFragment =
            when (item.getItemId()) {
                R.id.nav_structure -> Fragment_____Structure("Fragment title")
                R.id.nav_new_order -> _____OrderFragment()
                else -> Fragment_____Structure("Fragment title")
            }
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, selectedFragment).commit()

        return true
    }

    override fun sendData(message: Int) {
        var currentItem = getItem(+1)
        viewPager.currentItem = currentItem
        (viewPager.adapter as OrderSectionPagerAdapter).setParentId(message)
//        ToDo change livedata here!!!

        val tag = "android:switcher:" + R.id.fragment_container.toString() + ":" + 0
        val f = supportFragmentManager.findFragmentByTag(tag) as Fragment____Investigations?
//        f!!.displayReceivedData(message!!)
    }

    fun getItem(i: Int) = viewPager.currentItem+i
}

enum class Target(val tList: String) {
    DEPARTMENTS("DEPARTMENTS"),
    TEAM_MEMBERS("TEAM_MEMBERS"),
    ORDERS("ORDERS");

    companion object {
        const val cKey: String = "TARGET_LIST"
    }
}

interface SendMessage {
    fun sendData(message: Int)
}