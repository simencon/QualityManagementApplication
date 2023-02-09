package com.simenko.qmapp.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemSubOrderBinding
import com.simenko.qmapp.domain.DomainSubOrderComplete


class SubOrderClick(val block: (Int, View, DomainSubOrderComplete) -> Unit) {
    fun onClick(position: Int, view: View, order: DomainSubOrderComplete): Unit {
        return block(position, view, order)
    }
}

class SubOrderViewHolder(val viewDataBinding: ItemSubOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item___sub_order
    }
}

class SubOrderAdapter(
    val activity: FragmentActivity,
    val fragmentContext: Context,
    val callback: SubOrderClick
) :
    RecyclerView.Adapter<SubOrderViewHolder>(), Filterable {

    companion object {
        @JvmStatic
        var lastCheckedPos = -1

        @JvmStatic
        var lastCheckedView: View? = null
    }

    private var myFilter: MyFilter? = null

    var itemsList: List<DomainSubOrderComplete> = emptyList()
        set(value) {
            field = value
            itemsListFiltered.clear()
            itemsListFiltered.addAll(value)
            notifyDataSetChanged()
        }

    var itemsListFiltered: ArrayList<DomainSubOrderComplete> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getFilter(): Filter {
        if (myFilter == null) {
            myFilter = MyFilter(this, itemsList)
        }
        return myFilter as MyFilter
    }

    private class MyFilter(
        private val adapter: SubOrderAdapter,
        private val originalList: List<DomainSubOrderComplete>
    ) : Filter() {
        private val results: FilterResults = object : FilterResults() {}
        private val filteredList: ArrayList<DomainSubOrderComplete> = arrayListOf()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            filteredList.clear()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(originalList)
            } else {
                val filterPattern: Int = Integer.parseInt(constraint.toString())
                originalList.forEach {
                    if (it.subOrder.orderId == filterPattern) {
                        filteredList.add(it)
                    }
                }
            }
            results.values = filteredList
            results.count = filteredList.size

            return results
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            adapter.itemsListFiltered.clear()
            adapter.itemsListFiltered.addAll(results.values as List<DomainSubOrderComplete>)
            adapter.notifyDataSetChanged()
        }

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
            it.currentView = it.subOrderClickableOverlay
            it.order = itemsListFiltered[position]

            if (SubOrderAdapter.lastCheckedPos == position) {
                it.subOrderClickableOverlay.setBackgroundResource(R.drawable.background____selected_record)
            } else {
                it.subOrderClickableOverlay.setBackgroundResource(
                    AdapterUtils.getNormalBackground(
                        fragmentContext
                    ).resourceId
                )
            }

            it.orderCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return itemsListFiltered.size
    }
}