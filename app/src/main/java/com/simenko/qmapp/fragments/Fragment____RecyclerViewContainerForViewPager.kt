package com.simenko.qmapp.fragments

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
import com.simenko.qmapp.Activity_____Main
import com.simenko.qmapp.databinding.FragmentRvForViewPagerBinding
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

enum class TargetInv() {
    ORDERS,
    TASKS,
    ITEMS,
    RESULTS,
    FOR_TESTING
}

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "TARGET_LIST"
private const val ARG_PARAM2 = "PARENT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment____RecyclerViewContainerForViewPager.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment____RecyclerViewContainerForViewPager(
    val parentActivity: Fragment______ViewPagerContainer,
    var title: String
) :
    Fragment() {
    /**
     * Used lazy init due to the fact - is not possible to get the activity,
     * until the moment the view is created
     */
    private val TAG = "Fragment____Investigation"

    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as Activity_____Main).viewModel
        model
    }

    private var param1: String? = null
    private var param2: Int? = null
    lateinit var textView: TextView

    /**
     * Three actions done within recycler view adapter instantiation:
     * 1. Observe if all is fine with connection
     * 2. Observe specific live data
     * 3. Assign proper adapter
     */
    private val rvAdapter by lazy {
//        Start looking if all is fine with connection
        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        val resolvedBackground = AdapterUtils.getNormalBackground(requireContext())

        when (param1) {
            TargetInv.ORDERS.name -> {
//                Create adapter
                val rv = Adapter____Order(
                    OrderClick {order, position ->
                        order.detailsVisibility = !order.detailsVisibility
                        order.subOrdersVisibility = false
                        updateOneRvItem(position)
                    },
                    OrderSubOrdersClick {order, position ->
                        order.subOrdersVisibility = !order.subOrdersVisibility
                        updateOneRvItem(position)
                    },viewModel, viewLifecycleOwner
                )
//                Start looking for target live data
                viewModel.completeOrders.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            rv.itemsList = items
                        }
                    })
//                Assign adapter
                rv
            }
            TargetInv.TASKS.name -> {
                val rv = Adapter___SubOrder(
                    SubOrderClick {subOrder, position  ->
                        subOrder.detailsVisibility = !subOrder.detailsVisibility
                        updateOneRvItem(position)
                    },viewModel, viewLifecycleOwner
                )
                viewModel.completeSubOrders.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            rv.itemsList = items
                        }
                    }
                )
                rv
            }
            TargetInv.ITEMS.name -> {
                null
            }
            TargetInv.RESULTS.name -> {
                null
            }
            else -> null
        }
    }

    private fun updateOneRvItem(position: Int) {
        rvAdapter?.notifyItemChanged(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRvForViewPagerBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment____rv_for_view_pager,
            container,
            false
        )
        textView = binding.root.findViewById(R.id.title_investigations)
        val text = "$title:  $param1"
        textView.text = text

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.rv_investigations).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }
        requireContext().theme
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param targetList Parameter 1.
         * @param parentId Parameter 2.
         * @return A new instance of fragment Fragment____Investigations.
         */
        @JvmStatic
        fun newInstance(
            parentActivity: Fragment______ViewPagerContainer,
            title: String,
            targetList: TargetInv,
            parentId: Int
        ) =
            Fragment____RecyclerViewContainerForViewPager(parentActivity, title).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, targetList.name)
                    putInt(ARG_PARAM2, parentId)
                }
            }
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

}