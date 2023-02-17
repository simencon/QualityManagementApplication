package com.simenko.qmapp.ui.main.manufacturing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemSubDepartmentBinding
import com.simenko.qmapp.domain.DomainSubDepartment
import com.simenko.qmapp.ui.main.QualityManagementViewModel

class SubDepartmentClick(val block: (DomainSubDepartment, Int) -> Unit) {
    fun onClick(subDepartment: DomainSubDepartment, position: Int) = block(subDepartment, position)
}

class Adapter________SubDepartment(
    val callback: SubDepartmentClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SubDepartmentViewHolder>() {
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
            it.subDepartmentCallback = callback
            it.position = position

            val channelAdapter =
                Adapter_______Channel(ChannelClick { channel, position ->
                    channel.linesVisibility = !channel.linesVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = channelAdapter

            it.subDepartmentChannels.adapter = it.childAdapter

            this.viewModel.channels.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        channelAdapter.itemsList =
                            items.filter { item -> item.subDepId == itemsList[position].id }
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

class SubDepartmentViewHolder(val viewDataBinding: ItemSubDepartmentBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item________sub_department
    }

}