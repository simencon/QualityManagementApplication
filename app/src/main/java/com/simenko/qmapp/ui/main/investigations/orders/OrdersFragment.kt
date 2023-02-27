package com.simenko.qmapp.ui.main.investigations.orders

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentRvAndTitleBinding
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.QualityManagementViewModel
import com.simenko.qmapp.ui.main.investigations.InvestigationsContainerFragment
import com.simenko.qmapp.ui.main.team.TeamMembersLiveData
import com.simenko.qmapp.ui.theme.QMAppTheme
import com.simenko.qmapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class OrdersFragment(
    private var title: String
) :
    Fragment() {

    private val viewModel: QualityManagementViewModel by lazy {
        (activity as MainActivity).viewModel
    }

    private val rvAdapter by lazy {
        Adapter____Order(
            OrderClick { order, position ->
                order.detailsVisibility = !order.detailsVisibility
                order.subOrdersVisibility = false
                updateOneRvItem(position)
            },
            OrderSubOrdersClick { order, position ->
                order.subOrdersVisibility = !order.subOrdersVisibility
                updateOneRvItem(position)
            }, activity as Activity
        )
    }

    private fun updateOneRvItem(position: Int) {
        rvAdapter.notifyItemChanged(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRvAndTitleBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment____rv_and_title,
            container,
            false
        )
        binding.root.findViewById<TextView?>(R.id.title_investigations).text = title

        when (title) {
            InvestigationsContainerFragment.TargetInv.COMPOSE.name -> {
                binding.composeView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                binding.rvInvestigations.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
                )
            }
            InvestigationsContainerFragment.TargetInv.CLASSIC.name -> {
                binding.rvInvestigations.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                binding.composeView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0.0f
                )
            }
            else -> {}
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.rv_investigations).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        requireContext().theme

//        Start looking if all is fine with connection
        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

//        Start looking for target live data
        viewModel.completeOrders.observe(
            viewLifecycleOwner,
            Observer { items ->
                items?.apply {
                    rvAdapter.itemsList = items
                }
            })

        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                InvestigationsAll(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp, horizontal = 4.dp), viewModel
                )
            }
        }

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}