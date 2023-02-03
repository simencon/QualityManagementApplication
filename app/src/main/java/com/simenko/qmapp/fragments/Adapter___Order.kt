package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOrderBinding
import com.simenko.qmapp.databinding.ItemTeamMemberBinding
import com.simenko.qmapp.domain.DomainTeamMember
import com.simenko.qmapp.room_entities.DatabaseCompleteOrder

class OrderClick(val block: (DatabaseCompleteOrder) -> Unit) {
    fun onClick(order: DatabaseCompleteOrder): Unit {
        return block(order)
    }
}

class OrderViewHolder(val viewDataBinding: ItemOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item___order
    }
}

class OrderAdapter(val callback: OrderClick) :
    RecyclerView.Adapter<OrderViewHolder>() {

    var itemsList: List<DatabaseCompleteOrder> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val withDataBinding: ItemOrderBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                OrderViewHolder.LAYOUT,
                parent,
                false
            )
        return OrderViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.order = itemsList[position]
            it.orderCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}