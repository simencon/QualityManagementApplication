package com.simenko.qmapp.ui.main.investigations

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
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.databinding.FragmentPagerContainerBinding
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.utils.StringUtils

class InvestigationsContainerFragment(private val createdRecord: CreatedRecord? = null) : Fragment() {

    private lateinit var binding: FragmentPagerContainerBinding
    private lateinit var viewPager: ViewPager2
    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as MainActivity).viewModel
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
        val pagerAdapter = OrderSectionPagerAdapter(createdRecord, this)
        viewPager.adapter = pagerAdapter
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val tabLayout: TabLayout = binding.tabs
        TabLayoutMediator(tabLayout, viewPager, true, true) { tab, position ->
            tab.text = StringUtils.getWithSpaces(TargetInv.values()[position].name)
        }.attach()


        return binding.root
    }

    enum class TargetInv {
        TO_DO,
        IN_PROGRESS,
        DONE
    }
}