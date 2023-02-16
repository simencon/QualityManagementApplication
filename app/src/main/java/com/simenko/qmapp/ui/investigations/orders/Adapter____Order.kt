package com.simenko.qmapp.fragments

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOrderBinding
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.ui.QualityManagementViewModel
import com.simenko.qmapp.ui.MainActivity

class OrderClick(val block: (DomainOrderComplete, Int) -> Unit) {
    fun onClick(order: DomainOrderComplete, position: Int): Unit {
        return block(order, position)
    }
}

class OrderSubOrdersClick(val block: (DomainOrderComplete, Int) -> Unit) {
    fun onClick(order: DomainOrderComplete, position: Int): Unit {
        return block(order, position)
    }
}

class OrderViewHolder(val viewDataBinding: ItemOrderBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item____order
    }
}

private const val TAG = "Adapter____Order"

class Adapter____Order constructor(
    private val callbackOrderDetails: OrderClick,
    private val callbackOrderSubOrders: OrderSubOrdersClick,
    activity: Activity
) :
    RecyclerView.Adapter<OrderViewHolder>() {

    private val viewModel: QualityManagementViewModel
    private val lifecycleOwner: LifecycleOwner

    init {
        viewModel = (activity as MainActivity).viewModel
        lifecycleOwner = activity as MainActivity
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
            it.order = itemsList[position]
            it.orderCallback = callbackOrderDetails
            it.orderSubOrdersCallback = callbackOrderSubOrders
            it.position = position

            val subOrderAdapter =
                Adapter___SubOrder(SubOrderClick { subOrder, position ->
                    subOrder.detailsVisibility = !subOrder.detailsVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, SubOrderTasksClick { subOrder, position ->
                    subOrder.tasksVisibility = !subOrder.tasksVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = subOrderAdapter

            it.orderSubOrders.adapter = it.childAdapter

            this.viewModel.completeSubOrders.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        subOrderAdapter.itemsList =
                            items.filter { item -> item.subOrder.orderId == itemsList[position].order.id }
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