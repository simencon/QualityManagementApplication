package com.simenko.qmapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.ui.main.MainActivity
import com.simenko.qmapp.databinding.FragmentRvForMainActivityBinding
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

enum class Target (val title: String) {
    TEAM_MEMBERS ("Company employees"),
    DEPARTMENTS ("Company structure"),
    SUB_DEPARTMENTS ("Company products"),
    ORDERS ("Investigations");
}

private const val ARG_PARAM1 = "TARGET_LIST"

class Fragment____RecyclerViewForMainActivity(val title: String) : Fragment() {

    constructor() : this("restored") {

    }

    private var param1: String? = null

    /**
     * Used lazy init due to the fact - is not possible to get the activity,
     * until the moment the view is created
     */
    private val viewModel: QualityManagementViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        val model = (activity as MainActivity).viewModel
        model
    }

    private val rvAdapter by lazy {
        when (param1) {
            Target.TEAM_MEMBERS.name -> {
                Adapter___________TeamMember(
                    TeamMemberClick {
                        Toast.makeText(context, it.selectedRecord(), Toast.LENGTH_LONG).show()
                    }
                )
            }
            Target.DEPARTMENTS.name -> {
                Adapter__________Department(
                    DepartmentClick { item, position ->
                        item.departmentDetailsVisibility = !item.departmentDetailsVisibility
                        updateOneRvItem(position)
                    }, viewModel, viewLifecycleOwner
                )
            }
            Target.SUB_DEPARTMENTS.name -> {
                Adapter________SubDepartment(
                    SubDepartmentClick { subDepartment, position ->
                        subDepartment.channelsVisibility = !subDepartment.channelsVisibility
                        updateOneRvItem(position)
                    }, viewModel, viewLifecycleOwner
                )
            }
            else -> null
        }
    }

    private fun updateOneRvItem(position: Int) {
        rvAdapter?.notifyItemChanged(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        param1 = arguments?.getString(ARG_PARAM1).toString()
        val binding: FragmentRvForMainActivityBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_____rv_for_main_activity,
            container,
            false
        )

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
        }

        viewModel.eventNetworkError.observe(
            viewLifecycleOwner,
            Observer<Boolean> { isNetworkError ->
                if (isNetworkError) onNetworkError()
            })

        return binding.root
    }

    private fun onNetworkError() {
        if (!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (param1 == null) {
            param1 = arguments?.getString(ARG_PARAM1).toString()
        }


        when (param1) {
            Target.TEAM_MEMBERS.name -> {
                viewModel.teamMembers.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            (rvAdapter as Adapter___________TeamMember).itemsList = items
                        }
                    })
            }
            Target.DEPARTMENTS.name -> {
                viewModel.departmentsDetailed.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            (rvAdapter as Adapter__________Department).itemsList = items
                        }
                    })
            }
            Target.SUB_DEPARTMENTS.name -> {
                viewModel.subDepartments.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            (rvAdapter as Adapter________SubDepartment).itemsList = items
                        }
                    })
            }
            Target.ORDERS.name -> {
                viewModel.completeOrders.observe(
                    viewLifecycleOwner,
                    Observer { items ->
                        items?.apply {
                            (rvAdapter as Adapter____Order).itemsList = items
                        }
                    })
            }
            else -> {

            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param targetList Parameter 1.
         * @return A new instance of fragment Fragment____Structure.
         */
        @JvmStatic
        fun newInstance(
            title: String,
            targetList: Target,
        ) =
            Fragment____RecyclerViewForMainActivity(title).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, targetList.name)
                }
            }
    }
}
