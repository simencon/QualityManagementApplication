package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemDepartmentBinding
import com.simenko.qmapp.databinding.ItemSubDepartmentBinding
import com.simenko.qmapp.domain.DomainDepartmentComplete
import com.simenko.qmapp.domain.DomainSubDepartment

class SubDepartmentClick(val block: (DomainSubDepartment, Int) -> Unit) {
    fun onClick(subDepartment: DomainSubDepartment, position: Int) = block(subDepartment, position)
}

class Adapter____SubDepartment(val callback: SubDepartmentClick) :
    RecyclerView.Adapter<SubDepartmentViewHolder>()
    {
    var itemsList: List<DomainSubDepartment> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubDepartmentViewHolder {

        val withDataBinding: ItemSubDepartmentBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                SubDepartmentViewHolder.LAYOUT,
                parent,
                false
            )
        return SubDepartmentViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: SubDepartmentViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.subDepartment = itemsList[position]
            it.departmentCallback = callback
            it.position = position
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}

class SubDepartmentViewHolder(val viewDataBinding: ItemSubDepartmentBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_____sub_department
    }

}