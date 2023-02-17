package com.simenko.qmapp.ui.manufacturing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemOperationBinding
import com.simenko.qmapp.domain.DomainManufacturingOperation
import com.simenko.qmapp.ui.QualityManagementViewModel

class OperationClick(val block: (DomainManufacturingOperation, Int) -> Unit) {
    fun onClick(operation: DomainManufacturingOperation, position: Int) = block(operation, position)
}

class Adapter______Operation(
    val callback: OperationClick,
    val viewModel: QualityManagementViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<OperationViewHolder>() {
    var itemsList: List<DomainManufacturingOperation> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder {

        val withDataBinding: ItemOperationBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                OperationViewHolder.LAYOUT,
                parent,
                false
            )
        return OperationViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.manOperation = itemsList[position]
            it.manOperationCallback = callback
            it.position = position
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}

class OperationViewHolder(val viewDataBinding: ItemOperationBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item_____operation
    }

}