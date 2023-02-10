package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOperationBinding
import com.simenko.qmapp.databinding.ItemSubOrderBinding
import com.simenko.qmapp.databinding.ItemSubOrderTaskBinding
import com.simenko.qmapp.domain.DomainManufacturingOperation
import com.simenko.qmapp.domain.DomainSubOrderTaskComplete
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

class SubOrderTaskClick(val block: (DomainSubOrderTaskComplete, Int) -> Unit) {
    fun onClick(operation: DomainSubOrderTaskComplete, position: Int) = block(operation, position)
}

class Adapter__SubOrderTask(
    val callback: SubOrderTaskClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SubOrderTaskViewHolder>() {
    var itemsList: List<DomainSubOrderTaskComplete> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubOrderTaskViewHolder {

        val withDataBinding: ItemSubOrderTaskBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                SubOrderTaskViewHolder.LAYOUT,
                parent,
                false
            )
        return SubOrderTaskViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: SubOrderTaskViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.subOrderTask = itemsList[position]
            it.subOrderTaskCallback = callback
            it.position = position
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}

class SubOrderTaskViewHolder(val viewDataBinding: ItemSubOrderTaskBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item__sub_order_task
    }

}