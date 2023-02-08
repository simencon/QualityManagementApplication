package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemDepartmentBinding
import com.simenko.qmapp.domain.DomainDepartmentComplete

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