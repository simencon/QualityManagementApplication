package com.simenko.qmapp.ui.main.manufacturing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemDepartmentBinding
import com.simenko.qmapp.domain.DomainDepartmentComplete

class DepartmentClick(val block: (DomainDepartmentComplete, Int) -> Unit) {
    fun onClick(department: DomainDepartmentComplete, position: Int) = block(department, position)
}

private const val TAG = "Adapter_____Department"

class Adapter__________Department(
    val callback: DepartmentClick,
    val viewModel: ManufacturingViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<DepartmentViewHolder>() {

    var itemsList: List<DomainDepartmentComplete> = emptyList()
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
            it.department = itemsList[position]
            it.departmentCallback = callback
            it.position = position

            val subDepAdapter =
                Adapter________SubDepartment(SubDepartmentClick { subDepartment, position ->
                    subDepartment.channelsVisibility = !subDepartment.channelsVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = subDepAdapter

            it.departmentSubDepartments.adapter = it.childAdapter

            this.viewModel.subDepartments.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        subDepAdapter.itemsList =
                            items.filter { item -> item.depId == itemsList[position].departments.id }
                                .toList()
                    }
                }
            )
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}

class DepartmentViewHolder(val viewDataBinding: ItemDepartmentBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_________department
    }


}