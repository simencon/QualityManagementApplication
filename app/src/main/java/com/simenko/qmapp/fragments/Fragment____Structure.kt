package com.simenko.qmapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.FragmentDepartmentsBinding
import com.simenko.qmapp.domain.DomainDepartmentComplete
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

enum class Target {
    DEPARTMENTS,
    TEAM_MEMBERS,
    ORDERS;
}

private const val ARG_PARAM1 = "TARGET_LIST"

class Fragment____Structure (val title: String) : Fragment() {
    private var param1: String? = null
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

    private val rvAdapter by lazy {
        when (param1) {
            Target.DEPARTMENTS.name -> {
                DepartmentAdapter(
                    DepartmentClick {
                        Toast.makeText(context, it.selectedRecord(), Toast.LENGTH_LONG).show()
                    }
                )
            }
            Target.TEAM_MEMBERS.name -> {
                TeamMemberAdapter(
                    TeamMemberClick {
                        Toast.makeText(context, it.selectedRecord(), Toast.LENGTH_LONG).show()
                    }
                )
            }
            else -> null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        param1 = arguments?.getString(param1).toString()
        val binding: FragmentDepartmentsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment___departments,
            container,
            false
        )

        binding.root.findViewById<TextView>(R.id.fragment_title).text = title

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

        when(param1) {
            Target.DEPARTMENTS.name -> {
                viewModel.departmentsDetailed.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainDepartmentComplete>> { items ->
                        items?.apply {
                            (rvAdapter as DepartmentAdapter).itemsList = items
                        }
                    })
            }
            Target.TEAM_MEMBERS.name -> {
                viewModel.teamMembers.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainTeamMember>> { items ->
                        items?.apply {
                            (rvAdapter as TeamMemberAdapter).itemsList = items
                        }
                    })
            }
            Target.ORDERS.name -> {
                viewModel.completeOrders.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainOrderComplete>> { items ->
                        items?.apply {
                            (rvAdapter as OrderAdapter).itemsList = items
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
            Fragment____Structure(title).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, targetList.name)
                }
            }
    }
}

