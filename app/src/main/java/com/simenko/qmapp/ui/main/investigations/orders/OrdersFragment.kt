package com.simenko.qmapp.ui.main.investigations.orders

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentRvAndTitleBinding
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.ui.main.QualityManagementViewModel

class OrdersFragment(
    private var title: String
) :
    Fragment() {

    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as MainActivity).viewModel
        model
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
        rvAdapter?.notifyItemChanged(position)
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

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}