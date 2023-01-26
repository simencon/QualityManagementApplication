package com.simenko.qmapp.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.simenko.qmapp.R
import com.simenko.qmapp.databinding.ItemTeamMemberBinding
import com.simenko.qmapp.domain.DomainTeamMembers

class TeamMemberClick(val block: (DomainTeamMembers) -> Unit) {
    fun onClick(teamMember: DomainTeamMembers): Unit {
        return block(teamMember)
    }
}

class TeamMemberViewHolder(val viewDataBinding: ItemTeamMemberBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.item____team_member
    }
}

class TeamMemberAdapter(val callback: TeamMemberClick) :
    RecyclerView.Adapter<TeamMemberViewHolder>() {

    var itemsList: List<DomainTeamMembers> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamMemberViewHolder {

        val withDataBinding: ItemTeamMemberBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                TeamMemberViewHolder.LAYOUT,
                parent,
                false
            )
        return TeamMemberViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: TeamMemberViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.teamMember = itemsList[position]
            it.teamMemberCallback = callback
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

}