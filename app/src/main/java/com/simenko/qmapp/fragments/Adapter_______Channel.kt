package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemChannelBinding
import com.simenko.qmapp.domain.DomainManufacturingChannel
import com.simenko.qmapp.viewmodels.QualityManagementViewModel

class ChannelClick(val block: (DomainManufacturingChannel, Int) -> Unit) {
    fun onClick(channel: DomainManufacturingChannel, position: Int) = block(channel, position)
}

class Adapter_______Channel(
    val callback: ChannelClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ChannelViewHolder>() {
    var itemsList: List<DomainManufacturingChannel> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {

        val withDataBinding: ItemChannelBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                ChannelViewHolder.LAYOUT,
                parent,
                false
            )
        return ChannelViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.manChannel = itemsList[position]
            it.manChannelCallback = callback
            it.position = position

            val lineAdapter =
                Adapter_______Line(LineClick { line, position ->
                    line.operationVisibility = !line.operationVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = lineAdapter

            it.channelLines.adapter = it.childAdapter

            this.viewModel.lines.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        lineAdapter.itemsList =
                            items.filter { item -> item.chId == itemsList[position].id }
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

class ChannelViewHolder(val viewDataBinding: ItemChannelBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_______channel
    }

}