package com.simenko.qmapp.fragments

import android.app.Application
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.BaseApplication
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOrderBinding
import com.simenko.qmapp.domain.DomainOrderComplete
import com.simenko.qmapp.viewmodels.QualityManagementViewModel
import javax.inject.Inject

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

class Adapter____Order(
    private val callbackOrderDetails: OrderClick,
    private val callbackOrderSubOrders: OrderSubOrdersClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val application: Application?
) :
    RecyclerView.Adapter<OrderViewHolder>() {

    companion object {
        private const val TAG = "Adapter____Order"
    }

    @Inject
    lateinit var globalMessage: String

    init {
        if(application!=null) {
            var investigationsComponent = (application as BaseApplication).appComponent.investigationsComponent().create()
            investigationsComponent.inject(this)
            Log.d(TAG, "globalMessage is: $globalMessage")
        }
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
                },SubOrderTasksClick { subOrder, position ->
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

//            ToDo      To highlight latest item (later use for measurements results)
            /*if(OrderAdapter.lastCheckedPos == position) {
                it.orderClickableOverlay.setBackgroundResource(R.drawable.background____selected_record)
            } else {
                it.orderClickableOverlay.setBackgroundResource(AdapterUtils.getNormalBackground(requireNotNull(callbackImplementedIn.context)).resourceId)
            }*/

        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}

object AdapterUtils {
    fun getNormalBackground(context: Context): TypedValue {
        val outValue = TypedValue()
        context.theme.resolveAttribute(
            android.R.attr.selectableItemBackgroundBorderless,
            outValue,
            true
        )
        return outValue
    }

    fun getSelectedBackground(context: Context): TypedValue {
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.drawable.background____selected_record, outValue, true)
        return outValue
    }
}