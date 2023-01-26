package com.simenko.qmapp.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.Target
import com.simenko.qmapp.databinding.FragmentDepartmentsBinding
import com.simenko.qmapp.databinding.ItemDepartmentBinding
import com.simenko.qmapp.domain.DomainDepartment
import com.simenko.qmapp.domain.DomainTeamMembers
import com.simenko.qmapp.domain.ListOfItems
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

public class ___DepartmentFragment : Fragment() {
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

    lateinit var targetList: String

    private val viewModelAdapter by lazy {
        when (targetList) {
            Target.DEPARTMENTS.tList -> {
                DepartmentAdapter(
                    DepartmentClick {
                        Toast.makeText(context, it.selectedRecord(), Toast.LENGTH_LONG).show()
                    }
                )
            }
            Target.TEAM_MEMBERS.tList -> {
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
        targetList = arguments?.getString(Target.cKey).toString()
        val binding: FragmentDepartmentsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment___departments,
            container,
            false
        )

        binding.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        binding.root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = viewModelAdapter
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

        when(targetList) {
            Target.DEPARTMENTS.tList -> {
                viewModel.departments.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainDepartment>> { items ->
                        items?.apply {
                            (viewModelAdapter as DepartmentAdapter).itemsList = items
                        }
                    })
            }
            Target.TEAM_MEMBERS.tList -> {
                viewModel.teamMembers.observe(
                    viewLifecycleOwner,
                    Observer<List<DomainTeamMembers>> { items ->
                        items?.apply {
                            (viewModelAdapter as TeamMemberAdapter).itemsList = items
                        }
                    })
            }
            else -> {

            }
        }
    }
}

