package com.simenko.qmapp.ui.main.investigations

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentInvestigationsBinding
import com.simenko.qmapp.ui.main.CreatedRecord
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.steps.InvestigationsMainComposition
import com.simenko.qmapp.utils.StringUtils
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

private const val TAG = "InvestigationsFragment"

class InvestigationsFragment(private val createdRecord: CreatedRecord? = null) :
    Fragment() {

    private lateinit var binding: FragmentInvestigationsBinding

    private val viewModel: InvestigationsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as MainActivity).investigationsModel
        model
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment______investigations,
            container,
            false
        )

        val tabLayout: TabLayout = binding.tabs
        for (position in TargetInv.values().iterator()) {
            tabLayout.addTab(
                tabLayout.newTab()
                    .setText(StringUtils.getWithSpaces(position.name))
                    .setTag(position.name)
            )
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewModel.setCurrentStatusToShow(tab?.tag.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.addStatusesToSnapShot()
        viewModel.addTeamToSnapShot()
        viewModel.addMetrixesToSnapShot()
        viewModel.addProductTolerancesToSnapShot()
        viewModel.addComponentTolerancesToSnapShot()
        viewModel.addComponentInStageTolerancesToSnapShot()

        requireContext().theme

        binding.composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                Log.d(TAG, "onViewCreated: $createdRecord")
                InvestigationsMainComposition(
                    modifier = Modifier
                        .padding(
                            vertical = 2.dp,
                            horizontal = 2.dp
                        ),
                    createdRecord = createdRecord
                )
            }
        }
    }

    enum class TargetInv {
        ALL,
        TO_DO,
        IN_PROGRESS,
        DONE
    }
}