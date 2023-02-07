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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp._____MainActivity
import com.simenko.qmapp.databinding.FragmentInvestigationsBinding
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.room_entities.DatabaseOrderComplete
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

enum class TargetInv() {
    ORDERS,
    TASKS,
    ITEMS,
    RESULTS
}

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "TARGET_LIST"
private const val ARG_PARAM2 = "PARENT_ID"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment____Investigations.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment____Investigations(val parentActivity: _____MainActivity, var title: String) :
    Fragment() {
    /**
     * Used lazy init due to the fact - is not possible to get the activity,
     * until the moment the view is created
     */
    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {

        }
        ViewModelProvider(
            this, QualityManagementViewModel.Factory(activity.application)
        ).get(QualityManagementViewModel::class.java)
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
            Observer<Boolean> { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        val resolvedBackground = AdapterUtils.getNormalBackground(requireContext())

        when (param1) {
            TargetInv.ORDERS.name -> {
//                Create adapter
                val rv = OrderAdapter(parentActivity,
                    OrderClick { position, view, order ->
                        if (OrderAdapter.lastCheckedView != null) {
                            OrderAdapter.lastCheckedView!!.setBackgroundResource(resolvedBackground.resourceId)
                        }
                        OrderAdapter.lastCheckedView = view

                        view.setBackgroundResource(R.drawable.background____selected_record)

                        OrderAdapter.lastCheckedPos = position
                    }
                )
//                Start looking for target live data
                viewModel.completeOrders.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainOrderComplete>> { items ->
                        items?.apply {
                            (rv as OrderAdapter).itemsList = items
                        }
                    })
//                Assign adapter
                rv
            }
            TargetInv.TASKS.name -> {
//                Create adapter
                val rv = SubOrderAdapter(requireActivity(), requireContext(),
                    SubOrderClick { position, view, subOrder ->
                        if (SubOrderAdapter.lastCheckedView != null) {
                            SubOrderAdapter.lastCheckedView!!.setBackgroundResource(
                                resolvedBackground.resourceId
                            )
                        }
                        SubOrderAdapter.lastCheckedView = view

                        view.setBackgroundResource(R.drawable.background____selected_record)

                        SubOrderAdapter.lastCheckedPos = position
                    }
                )
//                Start looking for target live data
                viewModel.completeSubOrders.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainSubOrderComplete>> { items ->
                        items?.apply {
                            (rv as SubOrderAdapter).itemsList = items
                        }
                    })
//                Assign adapter
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
        // Inflate the layout for this fragment
        val binding: FragmentInvestigationsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_____investigations,
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
            parentActivity: _____MainActivity,
            title: String,
            targetList: TargetInv,
            parentId: Int
        ) =
            Fragment____Investigations(parentActivity, title).apply {
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

    fun displayReceivedData(msg: Int) {
        Toast.makeText(activity, "the message: $msg", Toast.LENGTH_SHORT).show()
    }

}