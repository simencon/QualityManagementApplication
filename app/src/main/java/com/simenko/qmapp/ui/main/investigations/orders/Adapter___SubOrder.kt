package com.simenko.qmapp.ui.main.investigations.orders

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemSubOrderBinding
import com.simenko.qmapp.domain.DomainSubOrderComplete
import com.simenko.qmapp.ui.main.QualityManagementViewModel


class SubOrderClick(val block: (DomainSubOrderComplete, Int) -> Unit) {
    fun onClick(subOrder: DomainSubOrderComplete, position: Int): Unit {
        return block(subOrder, position)
    }
}
class SubOrderTasksClick(val block: (DomainSubOrderComplete, Int) -> Unit) {
    fun onClick(subOrder: DomainSubOrderComplete, position: Int): Unit {
        return block(subOrder, position)
    }
}

class SubOrderViewHolder(val viewDataBinding: ItemSubOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item___sub_order
    }
}

private const val TAG = "Adapter___SubOrder"

class Adapter___SubOrder(
    private val callbackSubOrderDetails: SubOrderClick,
    private val callbackSubOrderTasks: SubOrderTasksClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<SubOrderViewHolder>(), Filterable {

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
        private val adapter: Adapter___SubOrder,
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
            it.subOrder = itemsListFiltered[position]
            it.subOrderCallback = callbackSubOrderDetails
            it.subOrderTasksCallback = callbackSubOrderTasks
            it.position = position

            val subOrderTaskAdapter =
                Adapter__SubOrderTask(SubOrderTaskClick() { task, position ->
//                    Later will be used for measurements results
//                    task.detailsVisibility = !task.detailsVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = subOrderTaskAdapter

            it.subOrderTasks.adapter = it.childAdapter

            this.viewModel.completeSubOrderTasks.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        subOrderTaskAdapter.itemsList =
                            items.filter { item ->
                                Log.d(TAG, "onBindViewHolder: childID: ${item.subOrderTask.subOrderId}/parentID: ${itemsList[position].subOrder.id}")
                                item.subOrderTask.subOrderId == itemsList[position].subOrder.id
                            }
                                .toList()
                    }
                }
            )

//            ToDo      To highlight latest item (later use for measurements results)
            /*if (Adapter__SubOrder.lastCheckedPos == position) {
                it.subOrderClickableOverlay.setBackgroundResource(R.drawable.background____selected_record)
            } else {
                it.subOrderClickableOverlay.setBackgroundResource(
                    AdapterUtils.getNormalBackground(
                        fragmentContext
                    ).resourceId
                )
            }*/


        }
    }

    override fun getItemCount(): Int {
        return itemsListFiltered.size
    }
}