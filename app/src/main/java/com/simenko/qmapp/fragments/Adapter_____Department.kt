package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemDepartmentBinding
import com.simenko.qmapp.domain.DomainDepartmentComplete
import com.simenko.qmapp.domain.DomainSubDepartment

class DepartmentClick(val block: (DomainDepartmentComplete, Int) -> Unit) {
    fun onClick(department: DomainDepartmentComplete, position: Int) = block(department, position)
}

class Adapter_____Department(val callback: DepartmentClick) :
    RecyclerView.Adapter<DepartmentViewHolder>()
    {
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
            it.parentId = itemsList[position].departments.id

            val subDepAdapter = Adapter____SubDepartment(SubDepartmentClick { subDepartment, position ->
                subDepartment.channelsVisibility = !subDepartment.channelsVisibility
                it.childAdapter?.notifyItemChanged(position)
            })

            it.childAdapter = subDepAdapter

//            ToDo - how to bring here live data?
            subDepAdapter.itemsList = listOf(
                DomainSubDepartment(1,1,"testAbbr1","testName1",1,false),
                DomainSubDepartment(1,1,"testAbbr2","testName2",2,false)
            )

            it.itemDetails.adapter = subDepAdapter
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
        val LAYOUT = R.layout.item______department
    }


}