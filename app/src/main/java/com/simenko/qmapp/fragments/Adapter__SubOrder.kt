package com.simenko.qmapp.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemSubOrderBinding
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.room_entities.DatabaseCompleteSubOrder


class SubOrderClick(val block: (Int, View, DomainSubOrderComplete) -> Unit) {
    fun onClick(position: Int, view: View, order: DomainSubOrderComplete): Unit {
        return block(position, view, order)
    }
}

class SubOrderViewHolder(val viewDataBinding: ItemSubOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item__sub_order
    }
}

class SubOrderAdapter(val activity: FragmentActivity, val fragmentContext: Context, val callback: SubOrderClick) :
    RecyclerView.Adapter<SubOrderViewHolder>() {

    companion object {
        @JvmStatic var lastCheckedPos = -1
        @JvmStatic var lastCheckedView: View? = null
    }

    var itemsList: List<DomainSubOrderComplete> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubOrderViewHolder {

        val withDataBinding: ItemSubOrderBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                SubOrderViewHolder.LAYOUT,
                parent,
                false
            )
        return SubOrderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: SubOrderViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.position = position
            it.currentView = it.clickableOverlay
            it.order = itemsList[position]

            if(SubOrderAdapter.lastCheckedPos == position) {
                it.clickableOverlay.setBackgroundResource(R.drawable.background____selected_record)
            } else {
                it.clickableOverlay.setBackgroundResource(AdapterUtils.getNormalBackground(fragmentContext).resourceId)
            }

            it.orderCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}