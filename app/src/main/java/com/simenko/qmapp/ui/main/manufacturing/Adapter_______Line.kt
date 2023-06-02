package com.simenko.qmapp.ui.main.manufacturing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemLineBinding
import com.simenko.qmapp.domain.DomainManufacturingLine

class LineClick(val block: (DomainManufacturingLine, Int) -> Unit) {
    fun onClick(line: DomainManufacturingLine, position: Int) = block(line, position)
}

class Adapter_______Line(
    val callback: LineClick,
    val viewModel: ManufacturingViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<LineViewHolder>() {
    var itemsList: List<DomainManufacturingLine> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {

        val withDataBinding: ItemLineBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                LineViewHolder.LAYOUT,
                parent,
                false
            )
        return LineViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.manLine = itemsList[position]
            it.manLineCallback = callback
            it.position = position

            val operationAdapter =
                Adapter______Operation(OperationClick() { operation, position ->
                    operation.detailsVisibility = !operation.detailsVisibility
                    it.childAdapter?.notifyItemChanged(position)
                }, viewModel, lifecycleOwner)

            it.childAdapter = operationAdapter

            it.channelLines.adapter = it.childAdapter

            this.viewModel.operations.observe(this.lifecycleOwner,
                Observer { items ->
                    items?.apply {
                        operationAdapter.itemsList =
                            items.filter { item -> item.lineId == itemsList[position].id }
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

class LineViewHolder(val viewDataBinding: ItemLineBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item______line
    }

}