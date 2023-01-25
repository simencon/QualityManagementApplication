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

    private var viewModelAdapter: DepartmentAdapter? = null

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

        viewModelAdapter = DepartmentAdapter(DepartmentClick {
            val packageManager = context?.packageManager ?: return@DepartmentClick
            Toast.makeText(context, it.selectedRecord, Toast.LENGTH_LONG).show()
        })

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
        viewModel.departments.observe(
            viewLifecycleOwner,
            Observer<List<DomainDepartment>> { departments ->
                departments?.apply {
                    viewModelAdapter?.departments = departments
                }
            })
    }

    private val DomainDepartment.selectedRecord: String
        get() {
            val depName = depName
            val depAbbr = depAbbr
            return "$depName ($depAbbr)"
        }
}

class DepartmentClick(val block: (DomainDepartment) -> Unit) {
    fun onClick(department: DomainDepartment) = block(department)
}

class DepartmentAdapter(val callback: DepartmentClick) :
    RecyclerView.Adapter<DepartmentViewHolder>() {

    var departments: List<DomainDepartment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {

        val withDataBinding: ItemDepartmentBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                DepartmentViewHolder.LAYOUT,
                parent,
                false
            )
        return DepartmentViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.department = departments[position]
            it.departmentCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return departments.size
    }

}

class DepartmentViewHolder(val viewDataBinding: ItemDepartmentBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_____department
    }
}