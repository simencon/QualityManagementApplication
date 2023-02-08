package com.simenko.qmapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.simenko.qmapp.R
import com.simenko.qmapp.Activity_____Main
import com.simenko.qmapp.databinding.FragmentPagerContainerBinding
import com.simenko.qmapp.pagers.OrderSectionPagerAdapter
import com.simenko.qmapp.pagers.ZoomOutPageTransformer
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

class Fragment______ViewPagerContainer : Fragment(), SendMessage {

    private lateinit var binding: FragmentPagerContainerBinding
    private lateinit var viewPager: ViewPager2
    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as Activity_____Main).viewModel
        model
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment______pager_container,
            container,
            false
        )
        // Instantiate a ViewPager2 and a PagerAdapter.
        viewPager = binding.fragmentContainer
        val pagerAdapter = OrderSectionPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager, true, true) { tab, position ->
            tab.text = TargetInv.values()[position].name
            position
        }.attach()

        return binding.root
    }

    override fun sendData(message: Int) {
        val currentItem = getItem(+1)
        viewPager.currentItem = currentItem
        viewModel.subOrderParentId.value = message
    }

    private fun getItem(i: Int) = viewPager.currentItem + i

}

interface SendMessage {
    fun sendData(message: Int)
}