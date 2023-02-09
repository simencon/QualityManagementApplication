package com.simenko.qmapp.fragments

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOrderBinding
import com.simenko.qmapp.domain.DomainOrderComplete


private lateinit var sm: SendMessage

class OrderClick(val block: (Int, View, DomainOrderComplete) -> Unit) {
    fun onClick(position: Int, view: View, order: DomainOrderComplete): Unit {
        sm.sendData(order.order.id)
        return block(position, view, order)
    }
}

class OrderViewHolder(val viewDataBinding: ItemOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item____order
    }
}

class OrderAdapter(private val callbackImplementedIn: Fragment______ViewPagerContainer, val callback: OrderClick) :
    RecyclerView.Adapter<OrderViewHolder>() {

    init {
        sm = callbackImplementedIn as SendMessage
    }

    companion object {
        @JvmStatic var lastCheckedPos = -1
        @JvmStatic var lastCheckedView: View? = null
    }

    var itemsList: List<DomainOrderComplete> = emptyList()
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
            it.position = position
            it.currentView = it.orderClickableOverlay
            it.order = itemsList[position]

            if(OrderAdapter.lastCheckedPos == position) {
                it.orderClickableOverlay.setBackgroundResource(R.drawable.background____selected_record)
            } else {
                it.orderClickableOverlay.setBackgroundResource(AdapterUtils.getNormalBackground(requireNotNull(callbackImplementedIn.context)).resourceId)
            }

            it.orderCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}

object AdapterUtils {
    fun getNormalBackground(context: Context): TypedValue {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        return outValue
    }

    fun getSelectedBackground(context: Context): TypedValue {
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.drawable.background____selected_record, outValue, true)
        return outValue
    }
}